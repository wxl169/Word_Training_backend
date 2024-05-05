package org.wxl.wordTraining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingJudgementDTO;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.vo.word.WordListVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingJudgementVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingTotalVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.IWordAnswerService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.service.WordTrainingService;
import org.wxl.wordTraining.utils.BeanCopyUtils;
import org.wxl.wordTraining.utils.Md5Util;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 单词训练
 * @author wxl
 */

@Service
public class WordTrainingServiceImpl implements WordTrainingService {
    private static final String REDIS_KEY_PREFIX = "word_training_";
    private final RedisTemplate redisTemplate;
    private final UserService userService;
    private final WordMapper wordMapper;
    private final IWordAnswerService wordAnswerService;
    private final Gson gson = new Gson();
    private final Random random = new Random();
    @Autowired
    public WordTrainingServiceImpl(RedisTemplate redisTemplate,UserService userService,WordMapper wordMapper,IWordAnswerService wordAnswerService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.wordMapper = wordMapper;
        this.wordAnswerService = wordAnswerService;
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
            }
        }
        //如果选择的难度是练习模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            wordTrainingTotalVO =  this.practice(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList(),userAccount);
        }

        //如果选择的难度是挑战模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            //检查当前用户的挑战次数是否充足
            wordTrainingTotalVO =  this.challenge(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList());
        }

        return wordTrainingTotalVO;
    }

    /**
     * 练习难度
     * @param mode 游戏模式
     * @param wordTypeList 单词类型
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingTotalVO practice(Integer mode, List<String> wordTypeList,String userAccount){
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
                    Set<String> wordTypeSet = new HashSet<>(wordTypeList);
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
        WordTrainingTotalVO wordTrainingVO = null;

        //如果是英语选义
        if (WordTrainingConstant.ENGLISH_SELECTIONS == mode ){
            //遍历单词列表
             wordTrainingVO = this.getEnglishSelections(filteredWordVOList, wordList,userAccount);
        }
        //如果是中文选义
        if (WordTrainingConstant.CHINESE_SELECTIONS == mode){

        }

        //如果是填空拼写
        if (WordTrainingConstant.WORD_SPELL == mode){

        }
        return wordTrainingVO;
    }

    /**
     * 挑战难度
     * @param mode 游戏模式
     * @param wordTypeList 单词类型
     * @return 单词训练数据
     */
    private  WordTrainingTotalVO challenge(Integer mode, List<String> wordTypeList) {
        //如果是英语选义
        if (WordTrainingConstant.ENGLISH_SELECTIONS == mode){

        }

        //如果是中文选义
        if (WordTrainingConstant.CHINESE_SELECTIONS == mode){

        }

        //如果是填空拼写
        if (WordTrainingConstant.WORD_SPELL == mode){

        }
        return null;
    }


    /**
     * 生成英语选义题目
     * @param filteredWordVOList 单词列表
     * @param wordList 未被筛选的单词信息
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingTotalVO getEnglishSelections(List<WordListVO> filteredWordVOList,List<Word> wordList,String userAccount){
        //redis的key
        String redisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:englishSelections:%s",userAccount);
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
            int oldSize = wordList.size();
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
        //设置过期时间
        redisTemplate.expire(redisKeyByTraining,WordTrainingConstant.EXPIRE_TIME, TimeUnit.SECONDS);
        WordTrainingVO wordTrainingVO = gson.fromJson((String) redisTemplate.opsForHash().get(redisKeyByTraining, "1"), WordTrainingVO.class);
        WordTrainingTotalVO wordTrainingTotalVO = new WordTrainingTotalVO();
        wordTrainingTotalVO.setWordTrainingVO(wordTrainingVO);
        wordTrainingTotalVO.setTotal(questionNumber  - 1);
        return wordTrainingTotalVO;
        }




    /**
     * 合并临时用户的题库数据到登录用户的题库中
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param userAccount 已登录的用户账号
     * @return 未操作的第一题
     */
    @Transactional(rollbackFor = Exception.class)
    public WordTrainingTotalVO mergeWordTrainingList(WordTrainingBeginRequest wordTrainingBeginRequest,String userAccount){
        //如果选择的难度是练习模式
        WordTrainingTotalVO wordTrainingTotalVO = null;

        //将临时用户的数据更改为当前登录用户的数据
        Map map = new HashMap();
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            //英语选义模式
            if (wordTrainingBeginRequest.getMode().equals(WordTrainingConstant.ENGLISH_SELECTIONS)){
                //获取临时用户的题库数据
                String oldRedisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:englishSelections:%s",wordTrainingBeginRequest.getTemporaryUserAccount());
                // 查询该key是否有数据
                Boolean hasKey = redisTemplate.hasKey(oldRedisKeyByTraining);
                if (hasKey != null && hasKey) {
                    // 如果有数据，修改key
                    String newRedisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:englishSelections:%s",userAccount);
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
                wordTrainingTotalVO.setTotal(map.size());
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

        WordTrainingJudgementVO wordTrainingJudgementVO = null;
        //如果选择的难度是练习模式
        if (wordTrainingJudgementDTO.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            wordTrainingJudgementVO =  this.getPracticeWordTrainingVO(wordTrainingJudgementDTO.getMode(),wordTrainingJudgementDTO.getQuestionNumber(),userAccount, wordTrainingJudgementDTO.getAnswer());
        }

        //如果选择的难度是挑战模式
        if (wordTrainingJudgementDTO.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            wordTrainingJudgementVO =  this.getChallengeWordTrainingVO(wordTrainingJudgementDTO.getMode(),wordTrainingJudgementDTO.getQuestionNumber(),userAccount, wordTrainingJudgementDTO.getAnswer());
        }

        return wordTrainingJudgementVO;
    }


    /**
     * 获取练习模式的单词训练数据
     *
     * @param mode 模式
     * @param questionNumber 题号
     * @param userAccount 账号
     * @param answer 答案
     * @return 单词训练数据
     */
    @Transactional(rollbackFor = Exception.class)
    public WordTrainingJudgementVO getPracticeWordTrainingVO(Integer mode, Integer questionNumber, String userAccount, String answer) {
        String redisKeyByTraining = null;
        //如果是英语选义
        if (WordTrainingConstant.ENGLISH_SELECTIONS == mode) {
            redisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:englishSelections:%s", userAccount);
        }
        //如果是中文选义
        if (WordTrainingConstant.CHINESE_SELECTIONS == mode) {
            redisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:chineseSelections:%s", userAccount);
        }
        //如果是填空拼写
        if (WordTrainingConstant.WORD_SPELL == mode) {
        redisKeyByTraining = String.format("wordTraining:getWordTrainingList:practice:wordSpell:%s", userAccount);
        }

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
        WordTrainingVO wordTrainingVO = gson.fromJson(wordTrainingVOStr, WordTrainingVO.class);
        //判断答案是否正确
        String newAnswer = Md5Util.encrypt(answer, REDIS_KEY_PREFIX);
        if (newAnswer.equals(wordTrainingVO.getAnswer())) {
            //如果答案正确，将答案置为1
            wordTrainingVO.setIsTrue(1);
            isTrue  = true;
            //将答案记录到redis中
            redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(questionNumber), gson.toJson(wordTrainingVO));
        }else{
            //如果答案错误，将答案置为0
            wordTrainingVO.setIsTrue(0);
            //错题原因
            wordTrainingVO.setErrorCause("词义未理解");
            //将答案记录到redis中
            redisTemplate.opsForHash().put(redisKeyByTraining, String.valueOf(questionNumber), gson.toJson(wordTrainingVO));
            //搜索正确单词Id
            wordTrainingJudgementVO.setWordId(wordTrainingVO.getWordId());
        }
        //获取下一题
        String wordTrainingVONextStr = (String) redisTemplate.opsForHash().get(redisKeyByTraining, String.valueOf(questionNumber + 1));
        //如果没有下一题，则结束游戏
        if (StringUtils.isBlank(wordTrainingVONextStr) || StringUtil.isEmpty(wordTrainingVONextStr)) {
            //结束游戏
            if (!userAccount.startsWith("游客")){
                //将游戏记录保持在数据库中
                Map<String,String> wordAnswerMap = redisTemplate.opsForHash().entries(redisKeyByTraining);
                if(wordAnswerMap == null || wordAnswerMap.isEmpty()){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题库数据为空!");
                }
                Boolean insert = wordAnswerService.saveWordAnswer(wordAnswerMap,userAccount,WordTrainingConstant.PRACTICE);
                if (!insert){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"保持用户答题记录失败");
                }
            }
            return  null;
        }
        //如果有下一题，则返回下一题
        wordTrainingVONext = gson.fromJson(wordTrainingVONextStr, WordTrainingVO.class);
        wordTrainingJudgementVO.setWordTrainingVO(wordTrainingVONext);
        wordTrainingJudgementVO.setIsTrue(isTrue);
        wordTrainingJudgementVO.setMode(mode);
        wordTrainingJudgementVO.setDifficulty(WordTrainingConstant.PRACTICE);
        wordTrainingJudgementVO.setTotal(Math.toIntExact(redisTemplate.opsForHash().size(redisKeyByTraining)));
        return wordTrainingJudgementVO;
    }

    /**
     * 获取挑战模式的单词训练数据
     *
     * @param mode 模式
     * @param questionNumber 题号
     * @param userAccount 账号
     * @param answer 答案
     * @return 单词训练数据
     */
    private WordTrainingJudgementVO getChallengeWordTrainingVO(Integer mode,Integer questionNumber,String userAccount,String answer){
        return null;
    }


}
