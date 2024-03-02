package org.wxl.wordTraining.service.impl;

import org.springframework.stereotype.Service;
import org.wxl.wordTraining.constant.WordTrainingConstant;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.service.WordTrainingService;

import java.util.List;

/**
 * 单词训练
 * @author wxl
 */

@Service
public class WordTrainingServiceImpl implements WordTrainingService {


    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的条件
     * @return 数据
     */
    @Override
    public Object getWordList(WordTrainingBeginRequest wordTrainingBeginRequest) {
        Object wordTrainingList = null;
        //如果选择的难度是练习模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.PRACTICE)){
            wordTrainingList =  this.practice(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList());
        }
        //如果选择的难度是挑战模式
        if (wordTrainingBeginRequest.getDifficulty().equals(WordTrainingConstant.CHALLENGE)){
             wordTrainingList =  this.challenge(wordTrainingBeginRequest.getMode(),wordTrainingBeginRequest.getWordTypeList());
        }

        return wordTrainingList;
    }



    /**
     * 练习难度
     * @param mode 游戏模式
     * @param wordTypeList 单词类型
     * @return 单词训练数据
     */
    private Object practice(Integer mode, List<String> wordTypeList){
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
     * 挑战难度
     * @param mode 游戏模式
     * @param wordTypeList 单词类型
     * @return 单词训练数据
     */
    private Object challenge(Integer mode, List<String> wordTypeList) {
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




}
