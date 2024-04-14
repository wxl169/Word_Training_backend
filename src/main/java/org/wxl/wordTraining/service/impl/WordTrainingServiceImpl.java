package org.wxl.wordTraining.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.mapper.WordMapper;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.entity.Word;
import org.wxl.wordTraining.model.vo.article.ArticleListVO;
import org.wxl.wordTraining.model.vo.word.WordListVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.IWordService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.service.WordTrainingService;
import org.wxl.wordTraining.utils.BeanCopyUtils;
import org.wxl.wordTraining.utils.Md5Util;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单词训练
 * @author wxl
 */

@Service
public class WordTrainingServiceImpl implements WordTrainingService {
    private static final String REDIS_KEY_PREFIX = "word_training_";
    private RedisTemplate redisTemplate;
    private UserService userService;
    private WordMapper wordMapper;
    private Gson gson;
    @Autowired
    public WordTrainingServiceImpl(RedisTemplate redisTemplate,UserService userService,WordMapper wordMapper,Gson gson) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.wordMapper = wordMapper;
        this.gson = gson;
    }

    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param request 请求
     * @return 单词数据
     */
    @Override
    public  WordTrainingVO getWordList(WordTrainingBeginRequest wordTrainingBeginRequest,HttpServletRequest request) {
        WordTrainingVO wordTraining = null;
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
        }
        //如果选择的难度是练习模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            wordTraining =  this.practice(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList(),userAccount);
        }

        //如果选择的难度是挑战模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
            //检查当前用户的挑战次数是否充足
            wordTraining =  this.challenge(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList());
        }

        return wordTraining;
    }



    /**
     * 练习难度
     * @param mode 游戏模式
     * @param wordTypeList 单词类型
     * @param userAccount 用户账号
     * @return 单词训练数据
     */
    private WordTrainingVO practice(Integer mode, List<String> wordTypeList,String userAccount){
        LambdaQueryWrapper<Word> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Word::getIsDelete, 0);
        List<Word> wordList = wordMapper.selectList(queryWrapper);
        List<WordListVO> filteredWordVOList = null;
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
        //redis的key
        String redisKeyByTraining = String.format("wordTraining:wordTraining:getWordTrainingList:%s",userAccount);
        // 查询该key是否有数据
        Boolean hasKey = redisTemplate.hasKey(redisKeyByTraining);
        if (hasKey != null && hasKey) {
            // 如果有数据，删除数据
            redisTemplate.delete(redisKeyByTraining);
        }

        WordTrainingVO wordTrainingVO = null;
        //如果是英语选义
        if (WordTrainingConstant.ENGLISH_SELECTIONS == mode ){
            //遍历单词列表
             wordTrainingVO = this.getEnglishselections(filteredWordVOList, redisKeyByTraining,wordList);
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
    private  WordTrainingVO challenge(Integer mode, List<String> wordTypeList) {
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
     * @param redisKyByTraining redis的key
     * @param wordList 未被筛选的单词信息
     * @return 单词训练数据
     */
    private WordTrainingVO getEnglishselections(List<WordListVO> filteredWordVOList,String redisKyByTraining,List<Word> wordList){
        int count = 1;
        for (WordListVO wordVO : filteredWordVOList){
            //生成单词训练数据
            WordTrainingVO wordTrainingVO = new WordTrainingVO();
            wordTrainingVO.setWord(wordVO.getWord());
            wordTrainingVO.setPronounceEnglish(wordVO.getPronounceEnglish());
            wordTrainingVO.setPronounceAmerica(wordVO.getPronounceAmerica());
            // 随机确定正确答案的位置
            Random random = new Random();
            int size = wordList.size();
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
            // 打乱选项的顺序
            Collections.shuffle(options);
            // 将选项分别设置到对应的问题属性上
            wordTrainingVO.setQuestionA(options.get(0));
            wordTrainingVO.setQuestionB(options.get(1));
            wordTrainingVO.setQuestionC(options.get(2));
            wordTrainingVO.setQuestionD(options.get(3));
            // 设置正确答案的 MD5 加密值
            wordTrainingVO.setAnswer(Md5Util.encrypt(String.valueOf(correctIndex + 1), REDIS_KEY_PREFIX));
            //将生产的题目保持在redis中
            redisTemplate.opsForHash().put(redisKyByTraining,String.valueOf(count),gson.toJson(wordTrainingVO));
            count++;
        }
        return  gson.fromJson((String) redisTemplate.opsForHash().get(redisKyByTraining, "1"), WordTrainingVO.class);
        }

    }
