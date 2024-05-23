package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingEndDTO;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingJudgementDTO;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.enums.WordTrainingDifficultyEnum;
import org.wxl.wordTraining.model.enums.WordTrainingModeEnum;
import org.wxl.wordTraining.model.vo.word.WordListVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingEndVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingJudgementVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingTotalVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.IWordAnswerService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.service.WordTrainingService;
import org.wxl.wordTraining.utils.BeanCopyUtils;
import org.wxl.wordTraining.utils.Md5Util;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 单词训练
 * @author wxl
 */

@Service
@Slf4j
public class WordTrainingServiceImpl implements WordTrainingService {
    private static final String REDIS_KEY_PREFIX = "word_training_";
    private final RedisTemplate redisTemplate;
    private final UserService userService;
    private final WordMapper wordMapper;
    private final IWordAnswerService wordAnswerService;
    private final Gson gson = new Gson();
    private final Random random = new Random();
    private final RedissonClient redissonClient;

    @Autowired
    public WordTrainingServiceImpl(RedisTemplate redisTemplate, UserService userService, WordMapper wordMapper, IWordAnswerService wordAnswerService, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.wordMapper = wordMapper;
        this.wordAnswerService = wordAnswerService;
        this.redissonClient = redissonClient;
    }

    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param request 请求
     * @return 单词数据
     */
    @Override
    public WordTrainingTotalVO getWordList(WordTrainingBeginRequest wordTrainingBeginRequest, HttpServletRequest request) {
        WordTrainingTotalVO wordTrainingTotalVO = null;
        String userAccount = null;
        //检查当前用户是否登录
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull == null){
            if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
                //如果是挑战模式，。必须登录
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"请先登录系统");
            }
            //暂时没有登录用户
            if (wordTrainingBeginRequest.getTemporaryUserAccount() == null){
                //没有临时用户
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            userAccount = wordTrainingBeginRequest.getTemporaryUserAccount();
        }else{
            //有登录用户
            userAccount = loginUserPermitNull.getUserAccount();
            if (wordTrainingBeginRequest.getTemporaryUserAccount() != null){
                //有临时用户，检查临时用户是否与登录用户一致
                //将临时用户的题库数据合并到登录用户的题库中
                wordTrainingTotalVO = this.mergeWordTrainingList(wordTrainingBeginRequest,userAccount);
                if (wordTrainingTotalVO != null){
                    return wordTrainingTotalVO;
                }
            }
        }

        //如果是挑战模式检查当前用户的挑战次数是否充足
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            // 只有一个线程能获取到锁
            RLock lock = redissonClient.getLock("wordTraining:user:getWordList:lock:"+userAccount);
            try {
                while (true){
                    if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                        //检查当前用户的挑战次数是否充足
                        int count = loginUserPermitNull.getChallengeNum();
                        if (count < 1){
                            throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前用户的挑战次数不足");
                        }
                        if (count >= 1){
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                  log.error("获取锁失败",e.getMessage());
            }finally {
                // 只能释放自己的锁
                if (lock.isHeldByCurrentThread()) {
                    System.out.println("unLock: " + Thread.currentThread().getId());
                    lock.unlock();
                }
            }
        }
        //处理单词数据
        LambdaQueryWrapper<Word> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Word::getIsDelete, 0);
        List<Word> wordList = wordMapper.selectList(queryWrapper);
        List<WordListVO> filteredWordVOList;
        //筛选数据
        filteredWordVOList = wordList.parallelStream().map(wordVO -> {
            WordListVO wordListVO = BeanCopyUtils.copyBean(wordVO, WordListVO.class);
            //处理翻译
            Map<String, String> translationMap = gson.fromJson(wordVO.getTranslation(), new TypeToken<Map<String, String>>() {}.getType());
            List<String> translationList = translationMap.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.toList());
            List<String> typeList = gson.fromJson(wordVO.getType(), new TypeToken<List<String>>(){}.getType());
            wordListVO.setType(typeList);
            wordListVO.setTranslation(translationList);
            return wordListVO;
        }).collect(Collectors.toList());
        //筛选单词类型
        filteredWordVOList = filteredWordVOList.stream()
                .filter(wordVO -> {
                    Set<String> wordTypeSet = new HashSet<>(wordTrainingBeginRequest.getWordTypeList());
                    return !wordVO.getType().isEmpty() && !Collections.disjoint(wordVO.getType(), wordTypeSet);
                })
                .collect(Collectors.toList());
        //如果总条数大于五十，随机获取五十条数据
        if (filteredWordVOList.size() > 50){
            // 打乱List中元素的顺序
            Collections.shuffle(filteredWordVOList);
            // 取前50条数据
            filteredWordVOList = filteredWordVOList.subList(0, 50);
        }else if (filteredWordVOList.isEmpty()){
            //如果没有数据，返回空列表
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"抱歉，暂无该类型组单词，请选择其他类型组");
        }

        //根据训练模式生成题库数据
        wordTrainingTotalVO = this.selectMode(wordTrainingBeginRequest.getDifficulty(),wordTrainingBeginRequest.getMode(),filteredWordVOList,wordList,userAccount);

        return wordTrainingTotalVO;
    }


    /**
     * 根据游戏模式生成题库
     * @param difficulty 游戏难度
     * @param mode 游戏模式
     * @param filteredWordVOList 过滤后的题库数据
     * @param wordList 总单词数据
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private  WordTrainingTotalVO selectMode(Integer difficulty, Integer mode, List<WordListVO> filteredWordVOList,List<Word> wordList,String userAccount) {
        WordTrainingTotalVO wordTrainingTotalVO = null;
        //如果是英语选义
        if (WordTrainingConstant.ENGLISH_SELECTIONS == mode){
//遍历单词列表
            wordTrainingTotalVO = this.getEnglishSelections(difficulty,filteredWordVOList, wordList,userAccount);
        }

        //如果是中文选义
        if (WordTrainingConstant.CHINESE_SELECTIONS == mode){
            wordTrainingTotalVO = this.getChineseSelections(difficulty,filteredWordVOList, wordList,userAccount);
        }

        //如果是填空拼写
        if (WordTrainingConstant.WORD_SPELL == mode){
            wordTrainingTotalVO = this.getWordSpell(difficulty,filteredWordVOList,userAccount);
        }
        return wordTrainingTotalVO;
    }


    /**
     * 生成英语选义题目
     * @param difficulty 难度
     * @param filteredWordVOList 单词列表
     * @param wordList 未被筛选的单词信息
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingTotalVO getEnglishSelections(Integer difficulty , List<WordListVO> filteredWordVOList,List<Word> wordList,String userAccount){
        //redis的key
        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(difficulty),WordTrainingModeEnum.ENGLISH_SELECTIONS,userAccount);
        // 查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.TRUE.equals(hasKey)) {
            // 如果有数据，删除数据
            redisTemplate.delete(redisKeyByTraining);
        }
        int questionNumber  = 1;
        for (WordListVO wordVO : filteredWordVOList){
            //生成单词训练数据
            WordTrainingVO wordTrainingVO = new WordTrainingVO();
            wordTrainingVO.setWordId(wordVO.getId());
            wordTrainingVO.setWord(wordVO.getWord());
            wordTrainingVO.setPronounceEnglish(wordVO.getPronounceEnglish());
            wordTrainingVO.setPronounceAmerica(wordVO.getPronounceAmerica());
            //删除当前单词的信息
            Word word = wordMapper.selectById(wordVO.getId());
            wordList.remove(word);
            // 词库总长度
            int size = wordList.size();
            //生成正确答案的位置
            int correctIndex = random.nextInt(4);
            // 生成四个选项
            List<String> options = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i == correctIndex) {
                    // 设置正确答案
                    int index = random.nextInt(wordVO.getTranslation().size());
                    options.add(wordVO.getTranslation().get(index));
                } else {
                    // 设置其他选项
                    String randomOption;
                    do {
                        // 从 wordList 中随机选择一个单词的翻译作为选项
                        Map<String, String> translationMap = gson.fromJson(wordList.get(random.nextInt(size)).getTranslation(), new TypeToken<Map<String, String>>() {}.getType());
                        List<String> keyList = new ArrayList<>(translationMap.keySet());
                        // 随机选择一个键
                        String randomKey = keyList.get(random.nextInt(keyList.size()));
                        // 根据键获取对应的值
                        String randomValue = translationMap.get(randomKey);
                        randomOption = randomKey + ":" + randomValue;
                    } while (options.contains(randomOption)); // 检查选项是否已经存在于列表中
                    options.add(randomOption);
                }
            }
            // 将选项分别设置到对应的问题属性上
            wordTrainingVO.setQuestionA(options.get(0));
            wordTrainingVO.setQuestionB(options.get(1));
            wordTrainingVO.setQuestionC(options.get(2));
            wordTrainingVO.setQuestionD(options.get(3));
            // 设置正确答案的 MD5 加密值
            wordTrainingVO.setAnswer(Md5Util.encrypt(String.valueOf(correctIndex + 1), REDIS_KEY_PREFIX));
            wordTrainingVO.setQuestionNumber(questionNumber);
            wordTrainingVO.setIsTrue(0);
            //将生产的题目保持在redis中
            redisTemplate.opsForHash().put(redisKeyByTraining,String.valueOf(questionNumber ),gson.toJson(wordTrainingVO));
            questionNumber ++;
            //将删除的单词数据重新添加进wordList中
            wordList.add(word);
        }
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.IS_TRAINING_END,false);
        //开始时间
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.BEGIN_TIME, LocalDateTime.now().toString());
        if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
            //添加生命值
            redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.HEALTH_VALUE,WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        }
        //设置过期时间
        redisTemplate.expire(redisKeyByTraining,WordTrainingConstant.EXPIRE_TIME, TimeUnit.SECONDS);
        WordTrainingVO wordTrainingVO = gson.fromJson((String) redisTemplate.opsForHash().get(redisKeyByTraining, "1"), WordTrainingVO.class);
        WordTrainingTotalVO wordTrainingTotalVO = new WordTrainingTotalVO();
        wordTrainingTotalVO.setWordTrainingVO(wordTrainingVO);
        wordTrainingTotalVO.setTotal(questionNumber  - 1);
        wordTrainingTotalVO.setHealthValue(WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        return wordTrainingTotalVO;
        }

    /**
     * 生成中文选义题目
     * @param difficulty 难度
     * @param filteredWordVOList 单词列表
     * @param wordList 未被筛选的单词信息
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingTotalVO getChineseSelections(Integer difficulty , List<WordListVO> filteredWordVOList,List<Word> wordList,String userAccount){
        //redis的key
        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(difficulty),WordTrainingModeEnum.CHINESE_SELECTIONS,userAccount);
        // 查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.TRUE.equals(hasKey)) {
            // 如果有数据，删除数据
            redisTemplate.delete(redisKeyByTraining);
        }
        int questionNumber  = 1;
        for (WordListVO wordVO : filteredWordVOList){
            //生成单词训练数据
            WordTrainingVO wordTrainingVO = new WordTrainingVO();
            wordTrainingVO.setWordId(wordVO.getId());
            int index = random.nextInt(wordVO.getTranslation().size());
            wordTrainingVO.setTranslation(wordVO.getTranslation().get(index));
            //处理word
            wordTrainingVO.setWord(wordVO.getWord());

            //删除当前单词的信息
            Word word = wordMapper.selectById(wordVO.getId());
            wordList.remove(word);
            // 词库总长度
            int size = wordList.size();
            //生成正确答案的位置
            int correctIndex = random.nextInt(4);
            // 生成四个选项
            List<String> options = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i == correctIndex) {
                    // 设置正确答案
                    options.add(wordVO.getWord());
                } else {
                    options.add(wordList.get(random.nextInt(size)).getWord());
                }
            }
            // 将选项分别设置到对应的问题属性上
            wordTrainingVO.setQuestionA(options.get(0));
            wordTrainingVO.setQuestionB(options.get(1));
            wordTrainingVO.setQuestionC(options.get(2));
            wordTrainingVO.setQuestionD(options.get(3));
            // 设置正确答案的 MD5 加密值
            wordTrainingVO.setAnswer(Md5Util.encrypt(String.valueOf(correctIndex + 1), REDIS_KEY_PREFIX));
            wordTrainingVO.setQuestionNumber(questionNumber);
            wordTrainingVO.setIsTrue(0);
            //将生产的题目保持在redis中
            redisTemplate.opsForHash().put(redisKeyByTraining,String.valueOf(questionNumber ),gson.toJson(wordTrainingVO));
            questionNumber ++;
            //将删除的单词数据重新添加进wordList中
            wordList.add(word);
        }
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.IS_TRAINING_END,false);
        //开始时间
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.BEGIN_TIME, LocalDateTime.now().toString());
        if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
            //添加生命值
            redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.HEALTH_VALUE,WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        }
        //设置过期时间
        redisTemplate.expire(redisKeyByTraining,WordTrainingConstant.EXPIRE_TIME, TimeUnit.SECONDS);
        WordTrainingVO wordTrainingVO = gson.fromJson((String) redisTemplate.opsForHash().get(redisKeyByTraining, "1"), WordTrainingVO.class);
        WordTrainingTotalVO wordTrainingTotalVO = new WordTrainingTotalVO();
        wordTrainingTotalVO.setWordTrainingVO(wordTrainingVO);
        wordTrainingTotalVO.setTotal(questionNumber  - 1);
        wordTrainingTotalVO.setHealthValue(WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        return wordTrainingTotalVO;
    }

    /**
     * 生成填词模式
     * @param difficulty 难度
     * @param filteredWordVOList 单词列表
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingTotalVO getWordSpell(Integer difficulty , List<WordListVO> filteredWordVOList,String userAccount){
        //redis的key
        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(difficulty),WordTrainingModeEnum.WORD_SPELL,userAccount);
        // 查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.TRUE.equals(hasKey)) {
            // 如果有数据，删除数据
            redisTemplate.delete(redisKeyByTraining);
        }
        int questionNumber  = 1;
        for (WordListVO wordVO : filteredWordVOList){
            //生成单词训练数据
            WordTrainingVO wordTrainingVO = new WordTrainingVO();
            wordTrainingVO.setWordId(wordVO.getId());
            wordTrainingVO.setTranslationList(wordVO.getTranslation());
            //处理word
            //计算单词的长度
            int wordSize   = wordVO.getWord().length();
            //需要转换为_的个数为单词长度的一半中的随机数，但是最少为1，最大为4
            int needChangeNum = Math.max(1, Math.min(4, random.nextInt(wordSize / 2) + 1));
            //转为_的起始位置
            int changeStartIndex = random.nextInt(wordSize - needChangeNum);
            // 从changeStartIndex开始，changeNum个位置需要转换为_
            StringBuilder wordBuilder = new StringBuilder(wordVO.getWord());
            // 记录被改变的字母组合
            StringBuilder changeBuilder = new StringBuilder();
            for (int i = 0; i < needChangeNum; i++) {
                changeBuilder.append(wordBuilder.charAt(changeStartIndex + i));
                if (i == 0) {
                    wordBuilder.setCharAt(changeStartIndex + i, '_');
                } else {
                    // 清除后续位置的字符
                    wordBuilder.setCharAt(changeStartIndex + i, ' ');
                }
            }
            //清除wordBuilder中的空格

            wordTrainingVO.setWord(wordBuilder.toString().replaceAll("\\s+", ""));

            // 词库总长度
            int length = WordTrainingConstant.WORD_COMBINATION.length;
            //去除相同的组合
            List<String> list = Arrays.stream(WordTrainingConstant.WORD_COMBINATION).filter(word -> {
                if (word.contentEquals(changeBuilder)) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            //生成正确答案的位置
            int correctIndex = random.nextInt(9);
            //随机生成九个随机数，但是九个数不能相同
            List<Integer> numberList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                int randomNum = random.nextInt(list.size());
                while (numberList.contains(randomNum)) {
                    randomNum = random.nextInt(list.size());
                }
                numberList.add(randomNum);
            }

            // 生成四个选项
            Set<String> options = new HashSet<>();
            for (int i = 0; i < 9; i++) {
                if (i == correctIndex) {
                    // 设置正确答案
                    options.add(changeBuilder.toString());
                } else {
                    options.add(list.get(numberList.get(i)));
                }
            }
            // 将选项分别设置到对应的问题属性上
            wordTrainingVO.setQuestionSet(options);
            // 设置正确答案的 MD5 加密值
            wordTrainingVO.setAnswer(Md5Util.encrypt(changeBuilder.toString(), REDIS_KEY_PREFIX));
            wordTrainingVO.setQuestionNumber(questionNumber);
            wordTrainingVO.setIsTrue(0);
            //将生产的题目保持在redis中
            redisTemplate.opsForHash().put(redisKeyByTraining,String.valueOf(questionNumber),gson.toJson(wordTrainingVO));
            questionNumber ++;
            //将删除的单词数据重新添加进wordList中
        }
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.IS_TRAINING_END,false);
        //开始时间
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.BEGIN_TIME, LocalDateTime.now().toString());
        if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
            //添加生命值
            redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.HEALTH_VALUE,WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        }
        //设置过期时间
        redisTemplate.expire(redisKeyByTraining,WordTrainingConstant.EXPIRE_TIME, TimeUnit.SECONDS);
        WordTrainingVO wordTrainingVO = gson.fromJson((String) redisTemplate.opsForHash().get(redisKeyByTraining, "1"), WordTrainingVO.class);
        WordTrainingTotalVO wordTrainingTotalVO = new WordTrainingTotalVO();
        wordTrainingTotalVO.setWordTrainingVO(wordTrainingVO);
        wordTrainingTotalVO.setTotal(questionNumber  - 1);
        wordTrainingTotalVO.setHealthValue(WordTrainingConstant.DEFAULT_HEALTH_VALUE);
        return wordTrainingTotalVO;
    }

    /**
     *
     * TODO 合并临时用户的题库数据到登录用户的题库中
     *
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param userAccount 已登录的用户账号
     * @return 未操作的第一题
     */
    @Transactional(rollbackFor = Exception.class)
    public WordTrainingTotalVO mergeWordTrainingList(WordTrainingBeginRequest wordTrainingBeginRequest,String userAccount){
        //如果选择的难度是练习模式
        WordTrainingTotalVO wordTrainingTotalVO = null;

        //将临时用户的数据更改为当前登录用户的数据4
        Map map = new HashMap();
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            //英语选义模式
            if (wordTrainingBeginRequest.getMode().equals(WordTrainingConstant.ENGLISH_SELECTIONS)){
                //获取临时用户的题库数据
                String oldRedisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s",WordTrainingDifficultyEnum.PRACTICE,WordTrainingModeEnum.ENGLISH_SELECTIONS,wordTrainingBeginRequest.getTemporaryUserAccount());
                // 查询该key是否有数据
                Boolean hasKey = redisTemplate.hasKey(oldRedisKeyByTraining);
                if (hasKey != null && hasKey) {
                    // 如果有数据，修改key
                    String newRedisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s",WordTrainingDifficultyEnum.PRACTICE,WordTrainingModeEnum.ENGLISH_SELECTIONS,userAccount);
                    map = redisTemplate.opsForHash().entries(oldRedisKeyByTraining);
                    redisTemplate.opsForHash().putAll(newRedisKeyByTraining,map);
                    redisTemplate.delete(oldRedisKeyByTraining);
                }
            }
            //中文选义模式
        }

        //如果选择的难度是挑战模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            //检查当前用户的挑战次数是否充足
        }

        if (map.isEmpty()){
            return  null;
        }

        for (Map.Entry<String, WordTrainingVO> entry : (Iterable<Map.Entry<String, WordTrainingVO>>) map.entrySet()) {
            WordTrainingVO wordTrainingVO = entry.getValue();
            if (wordTrainingVO.getIsTrue() == 0) {
                wordTrainingTotalVO = new WordTrainingTotalVO();
                wordTrainingTotalVO.setWordTrainingVO(wordTrainingVO);
                if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
                    wordTrainingTotalVO.setTotal(map.size() - 2);
                }else{
                    wordTrainingTotalVO.setTotal(map.size() - 3);
                }
                return wordTrainingTotalVO;
            }
        }
        return null;
    }


    /**
     * 判断答案是否正确
     * @param wordTrainingJudgementDTO 答案
     * @param request 获取登录用户信息
     * @return
     */
    @Override
    public WordTrainingJudgementVO doJudgement(WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request) {
        //判断用户是否登录
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        String userAccount = null;
        if (loginUserPermitNull == null || loginUserPermitNull.getUserAccount() == null){
            //暂时没有登录用户
            if (wordTrainingJudgementDTO.getTemporaryUserAccount() == null){
                //没有临时用户
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"暂未生成临时账号,请退出游戏界面!");
            }
            userAccount = wordTrainingJudgementDTO.getTemporaryUserAccount();
        }else{
            userAccount = loginUserPermitNull.getUserAccount();
        }
        WordTrainingJudgementVO wordTrainingJudgementVO = this.getWordTrainingVO(wordTrainingJudgementDTO.getDifficulty(),wordTrainingJudgementDTO.getMode(),wordTrainingJudgementDTO.getQuestionNumber(),userAccount, wordTrainingJudgementDTO.getAnswer());

        return wordTrainingJudgementVO;
    }

    /**
     * 获取单词训练数据
     *
     * @param difficulty 难度
     * @param mode 模式
     * @param questionNumber 题号
     * @param userAccount 账号
     * @param answer 答案
     * @return 单词训练数据
     */
    @Transactional(rollbackFor = Exception.class)
    public WordTrainingJudgementVO getWordTrainingVO(Integer difficulty, Integer mode, Integer questionNumber, String userAccount, String answer) {
        String redisKeyByTraining = null;
        redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(difficulty), WordTrainingModeEnum.getEnumByValue(mode), userAccount);
        //下一题
        WordTrainingVO wordTrainingVONext = null;
        //是否正确
        boolean isTrue = false;
        //返回值
        WordTrainingJudgementVO wordTrainingJudgementVO = new WordTrainingJudgementVO();

        //从redis中获取题目数据
        String wordTrainingVOStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining, String.valueOf(questionNumber));
        if (wordTrainingVOStr == null || StringUtil.isEmpty(wordTrainingVOStr)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题库数据为空!");
        }
        int healthValue = 0;
        WordTrainingVO wordTrainingVO = gson.fromJson(wordTrainingVOStr, WordTrainingVO.class);
        //判断答案是否正确
        String newAnswer = Md5Util.encrypt(answer, REDIS_KEY_PREFIX);
        if (newAnswer.equals(wordTrainingVO.getAnswer())) {
            //如果答案正确，将答案置为
            wordTrainingVO.setIsTrue(1);
            wordTrainingVO.setErrorCause("");
            isTrue  = true;
            //将答案记录到redis中
            redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(questionNumber), gson.toJson(wordTrainingVO));
        }else{
            //如果答案错误，将答案置为2
            wordTrainingVO.setIsTrue(2);
            //错题原因
            if (mode == WordTrainingConstant.ENGLISH_SELECTIONS){
                wordTrainingVO.setErrorCause("词义未理解");
            }else if (mode == WordTrainingConstant.CHINESE_SELECTIONS){
                wordTrainingVO.setErrorCause("词汇未掌握");
            }else if (mode == WordTrainingConstant.WORD_SPELL){
                wordTrainingVO.setErrorCause("拼写错误");
            }
            //如果是挑战模式，则扣除生命值
            if (difficulty.equals(WordTrainingConstant.CHALLENGE)) {
                // 只有一个线程能获取到锁
                RLock lock = redissonClient.getLock("wordTraining:user:getWordList:health:lock:"+userAccount);
                try {
                    while (true){
                        if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                             healthValue = (int) redisTemplate.opsForHash().get(redisKeyByTraining, WordTrainingConstant.HEALTH_VALUE);
                            if (healthValue > 1) {
                                healthValue--;
                                redisTemplate.opsForHash().put(redisKeyByTraining, WordTrainingConstant.HEALTH_VALUE, healthValue);
                                break;
                            }else{
                                    //将答案记录到redis中
                                  redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(questionNumber), gson.toJson(wordTrainingVO));
                                  this.endTraining(redisKeyByTraining,userAccount,difficulty);
                                  return  null;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("获取锁失败",e.getMessage());
                }finally {
                    // 只能释放自己的锁
                    if (lock.isHeldByCurrentThread()) {
                        System.out.println("unLock: " + Thread.currentThread().getId());
                        lock.unlock();
                    }
                }
            }
            //将答案记录到redis中
            redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(questionNumber), gson.toJson(wordTrainingVO));
            //搜索正确单词Id
            wordTrainingJudgementVO.setWordId(wordTrainingVO.getWordId());
        }
        //获取下一题
        String wordTrainingVONextStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining, String.valueOf(questionNumber + 1));
        //如果没有下一题，则结束游戏
        if (StringUtils.isBlank(wordTrainingVONextStr) || StringUtil.isEmpty(wordTrainingVONextStr)) {
            this.endTraining(redisKeyByTraining,userAccount,difficulty);
            if (difficulty.equals(WordTrainingConstant.CHALLENGE) && (healthValue + 1) > 0 && !newAnswer.equals(wordTrainingVO.getAnswer())){
                //返回当前这题数据，直到次数用完
                wordTrainingJudgementVO.setWordTrainingVO(wordTrainingVO);
                wordTrainingJudgementVO.setIsTrue(isTrue);
                wordTrainingJudgementVO.setMode(mode);
                wordTrainingJudgementVO.setDifficulty(difficulty);
                wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining) - 3));
                wordTrainingJudgementVO.setHealthValue(healthValue);
                wordTrainingJudgementVO.setWordId(wordTrainingVO.getWordId());
                return wordTrainingJudgementVO;
            }else{
                return null;
            }
        }
        //如果有下一题，则返回下一题
        wordTrainingVONext = gson.fromJson(wordTrainingVONextStr, WordTrainingVO.class);
        wordTrainingJudgementVO.setWordTrainingVO(wordTrainingVONext);
        wordTrainingJudgementVO.setIsTrue(isTrue);
        wordTrainingJudgementVO.setMode(mode);
        wordTrainingJudgementVO.setDifficulty(difficulty);
        if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
            wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining) - 3));
            wordTrainingJudgementVO.setHealthValue(healthValue);
        }else {
            wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining) - 2));
        }
        return wordTrainingJudgementVO;
    }

    /**
     * 结束训练
     * @param redisKeyByTraining redisKey
     * @param userAccount 用户账号
     */

    private void endTraining(String redisKeyByTraining,String userAccount,Integer difficulty){
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.IS_TRAINING_END,true);
        redisTemplate.opsForHash().put(redisKeyByTraining,WordTrainingConstant.END_TIME, LocalDateTime.now().toString());
        //结束游戏
        if (!userAccount.startsWith("游客")){
            //将游戏记录保持在数据库中
            Map<String,Object> wordAnswerMap = redisTemplate.opsForHash().entries(redisKeyByTraining);
            wordAnswerMap.remove(WordTrainingConstant.IS_TRAINING_END);
            wordAnswerMap.remove(WordTrainingConstant.BEGIN_TIME);
            wordAnswerMap.remove(WordTrainingConstant.END_TIME);
            if (difficulty.equals(WordTrainingConstant.CHALLENGE)){
                wordAnswerMap.remove(WordTrainingConstant.HEALTH_VALUE);
            }
            if(wordAnswerMap == null || wordAnswerMap.isEmpty()){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题库数据为空!");
            }
             String wordTrainingVOStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining,"1");
             WordTrainingVO wordTrainingVO = gson.fromJson(wordTrainingVOStr, WordTrainingVO.class);
            if(wordTrainingVO.getIsTrue() != 0){
                Boolean insert = wordAnswerService.saveWordAnswer(wordAnswerMap,userAccount,difficulty);
                if (!insert){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"保存用户答题记录失败");
                }
            }
            //挑战次数减1
            if (difficulty.equals(WordTrainingConstant.CHALLENGE)) {
                RLock lock = redissonClient.getLock("wordTraining:user:getWordList:lock:" + userAccount);
                try {
                    while (true){
                        if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                            updateWrapper.eq(User::getUserAccount, userAccount)
                                    .eq(User::getIsDelete,0)
                                    .setSql("challenge_num = challenge_num - 1");
                            boolean update = userService.update(updateWrapper);
                            if (!update) {
                                throw new BusinessException(ErrorCode.OPERATION_ERROR, "挑战次数减1失败!");
                            }else{
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("获取锁失败",e.getMessage());
                }finally {
                    // 只能释放自己的锁
                    if (lock.isHeldByCurrentThread()) {
                        System.out.println("unLock: " + Thread.currentThread().getId());
                        lock.unlock();
                    }
                }
            }
        }
    }

    /**
     * 结算训练结果
     * @param wordTrainingEndDTO 当前训练信息
     * @param request 获取当前登录用户
     * @return 训练结果信息
     */
    @Override
    public WordTrainingEndVO balanceTraining(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request) throws ParseException {
       //获取redisKey
        String redisKeyByTraining = this.getRedisKey(wordTrainingEndDTO,request);
        // 如果有数据，获取数据
        Map<String, Object> map = redisTemplate.opsForHash().entries(redisKeyByTraining);
        if (map.isEmpty()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"暂无训练记录!");
        }
        WordTrainingEndVO wordTrainingEndVO = new WordTrainingEndVO();
        if (wordTrainingEndDTO.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            wordTrainingEndVO.setTotal(map.size() - 4);
        }else{
            wordTrainingEndVO.setTotal(map.size() - 3);
        }

        //正确的单词集合
        Map<Long,String> correctWordMap = new HashMap<>();
        int correctCount = 0;
        //错误的单词集合
        Map<Long,String> errorWordMap = new HashMap<>();
        int errorCount = 0;
        //完成的题目数量
        int finishNum = 0;
        //积分数
        int score = 0;
        Gson gson = new Gson();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!entry.getKey().equals(WordTrainingConstant.IS_TRAINING_END) && !entry.getKey().equals(WordTrainingConstant.BEGIN_TIME) && !entry.getKey().equals(WordTrainingConstant.END_TIME) && !entry.getKey().equals(WordTrainingConstant.HEALTH_VALUE)){
                WordTrainingVO wordTrainingVO = gson.fromJson(String.valueOf(entry.getValue()),WordTrainingVO.class);
                if (wordTrainingVO.getIsTrue() == 1) {
                    correctCount++;
                    finishNum++;
                    if(wordTrainingEndDTO.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
                        score++;
                    }
                    if (WordTrainingConstant.CHINESE_SELECTIONS == wordTrainingEndDTO.getMode()){
                        correctWordMap.put(wordTrainingVO.getWordId(), wordTrainingVO.getTranslation());
                    }else{
                        correctWordMap.put(wordTrainingVO.getWordId(), wordTrainingVO.getWord());
                    }
                } else if (wordTrainingVO.getIsTrue() == 2){
                    errorCount++;
                    finishNum++;
                    if (WordTrainingConstant.CHINESE_SELECTIONS == wordTrainingEndDTO.getMode()){
                        errorWordMap.put(wordTrainingVO.getWordId(), wordTrainingVO.getTranslation());
                    } else {
                        errorWordMap.put(wordTrainingVO.getWordId(), wordTrainingVO.getWord());
                    }
                }
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date beginTime = df.parse(map.get(WordTrainingConstant.BEGIN_TIME).toString());
        Date endTime = df.parse(map.get(WordTrainingConstant.END_TIME).toString());

        long diffInMillies = Math.abs(endTime.getTime() - beginTime.getTime());
        long diffInMinutes = TimeUnit.MILLISECONDS.toSeconds(diffInMillies);

        wordTrainingEndVO.setFinishTime(diffInMinutes);
        wordTrainingEndVO.setCorrectWordMap(correctWordMap);
        wordTrainingEndVO.setErrorWordMap(errorWordMap);
        wordTrainingEndVO.setCorrectNum(correctCount);
        wordTrainingEndVO.setErrorNum(errorCount);
        wordTrainingEndVO.setScore(score);
        wordTrainingEndVO.setFinishNum(finishNum);
        return wordTrainingEndVO;
    }

    /**
     * 结算单词训练
     * @param wordTrainingEndDTO 结算信息
     * @param request 获取当前登录用户
     * @return 结算结果
     */
    @Override
    public Boolean settlementWordTraining(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request) {
        //获取redisKey
        String redisKeyByTraining = this.getRedisKey(wordTrainingEndDTO,request);
        //删除redis中数据
        Boolean delete = redisTemplate.delete(redisKeyByTraining);
        if (!delete){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除训练记录失败!");
        }
        return true;
    }

    /**
     * 在训练过程中结束训练
     * @param wordTrainingEndDTO 训练信息
     * @param request 获取当前登录用户
     * @return 是否结束成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean endTrainingInBegin(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request) {
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (StringUtils.isBlank(wordTrainingEndDTO.getTemporaryUserAccount()) && loginUserPermitNull == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"暂无训练记录");
        }
        String userAccount = null;
        if (StringUtils.isNotBlank(wordTrainingEndDTO.getTemporaryUserAccount())){
            userAccount = wordTrainingEndDTO.getTemporaryUserAccount();
        }else if (StringUtils.isNotBlank(loginUserPermitNull.getUserAccount())){
            userAccount = loginUserPermitNull.getUserAccount();
        }

        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(wordTrainingEndDTO.getDifficulty()),WordTrainingModeEnum.getEnumByValue(wordTrainingEndDTO.getMode()),userAccount);
        //查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.FALSE.equals(hasKey)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"暂无训练记录");
        }
        this.endTraining(redisKeyByTraining,userAccount,wordTrainingEndDTO.getDifficulty());
        return true;
    }

    /**
     * 单词训练时间结束
     *
     * @param wordTrainingJudgementDTO 答案
     * @param request    获取登录用户信息
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WordTrainingJudgementVO wordTrainingTimeEnd(WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request) {
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请先登录!");
        }
        String userAccount = loginUserPermitNull.getUserAccount();
        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(wordTrainingJudgementDTO.getDifficulty()),WordTrainingModeEnum.getEnumByValue(wordTrainingJudgementDTO.getMode()),userAccount);
        //查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.FALSE.equals(hasKey)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"暂无训练记录");
        }
        //根据题号查看是否有数据
        String wordTrainingVOStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining, String.valueOf(wordTrainingJudgementDTO.getQuestionNumber()));
        if (wordTrainingVOStr == null || StringUtil.isEmpty(wordTrainingVOStr)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题库数据为空!");
        }

        int healthValue = 0;
        WordTrainingVO wordTrainingVO = gson.fromJson(wordTrainingVOStr, WordTrainingVO.class);
        wordTrainingVO.setIsTrue(2);
        wordTrainingVO.setErrorCause("答题超时");
        //将答案记录到redis中
        redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(wordTrainingJudgementDTO.getQuestionNumber()), gson.toJson(wordTrainingVO));
        // 只有一个线程能获取到锁
        RLock lock = redissonClient.getLock("wordTraining:user:getWordList:health:lock:" + userAccount);
        try {
            while (true){
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                        healthValue = (int) redisTemplate.opsForHash().get(redisKeyByTraining, WordTrainingConstant.HEALTH_VALUE);
                        if (healthValue > 0) {
                            healthValue--;
                            redisTemplate.opsForHash().put(redisKeyByTraining, WordTrainingConstant.HEALTH_VALUE, healthValue);
                            break;
                        }else{
                            //将答案记录到redis中
                            redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(wordTrainingJudgementDTO.getQuestionNumber()), gson.toJson(wordTrainingVO));
                            this.endTraining(redisKeyByTraining,userAccount,wordTrainingJudgementDTO.getDifficulty());
                            return  null;
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error("获取锁失败",e.getMessage());
            }finally {
                // 只能释放自己的锁
                if (lock.isHeldByCurrentThread()) {
                    System.out.println("unLock: " + Thread.currentThread().getId());
                    lock.unlock();
                }
            }
        //获取下一题
        String wordTrainingVONextStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining, String.valueOf(wordTrainingJudgementDTO.getQuestionNumber() + 1));
        //如果没有下一题，则结束游戏
        if (StringUtils.isBlank(wordTrainingVONextStr) || StringUtil.isEmpty(wordTrainingVONextStr)) {
            this.endTraining(redisKeyByTraining,userAccount,wordTrainingJudgementDTO.getDifficulty());
            return null;
        }
        WordTrainingVO wordTrainingVONext = gson.fromJson(wordTrainingVONextStr, WordTrainingVO.class);
        WordTrainingJudgementVO wordTrainingJudgementVO = new WordTrainingJudgementVO();
        wordTrainingJudgementVO.setWordTrainingVO(wordTrainingVONext);
        wordTrainingJudgementVO.setIsTrue(false);
        wordTrainingJudgementVO.setMode(wordTrainingJudgementDTO.getMode());
        wordTrainingJudgementVO.setDifficulty(wordTrainingJudgementDTO.getDifficulty());
        if (wordTrainingJudgementDTO.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining) - 3));
            wordTrainingJudgementVO.setHealthValue(healthValue);
        }else {
            wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining) - 2));
        }
        wordTrainingJudgementVO.setWordId(wordTrainingVO.getWordId());
        return wordTrainingJudgementVO;
    }


    /**
     * 获取redisKey
     *
     * @param wordTrainingEndDTO 训练信息
     * @param request 获取当前登录用户
     * @return redisKey
     */
    private String getRedisKey(WordTrainingEndDTO wordTrainingEndDTO,HttpServletRequest request){
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (StringUtils.isBlank(wordTrainingEndDTO.getTemporaryUserAccount()) && loginUserPermitNull == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"暂无训练记录");
        }
        String userAccount = null;
        if (StringUtils.isNotBlank(wordTrainingEndDTO.getTemporaryUserAccount())){
            userAccount = wordTrainingEndDTO.getTemporaryUserAccount();
        }else if (StringUtils.isNotBlank(loginUserPermitNull.getUserAccount())){
            userAccount = loginUserPermitNull.getUserAccount();
        }

        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:%s:%s:%s", WordTrainingDifficultyEnum.getEnumByValue(wordTrainingEndDTO.getDifficulty()),WordTrainingModeEnum.getEnumByValue(wordTrainingEndDTO.getMode()),userAccount);
        //查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (Boolean.FALSE.equals(hasKey)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"暂无训练记录");
        }
        boolean isTrainingEnd = (boolean) redisTemplate.opsForHash().get(redisKeyByTraining, WordTrainingConstant.IS_TRAINING_END);
        if (!isTrainingEnd){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请先完成本次训练!");
        }
        return redisKeyByTraining;
    }




}
