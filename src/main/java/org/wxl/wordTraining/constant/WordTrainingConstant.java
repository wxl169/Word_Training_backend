package org.wxl.wordTraining.constant;

/**
 * 英语单词练习常量
 * @author wxl
 */
public interface WordTrainingConstant {
    // --------------------------------------- 单词模式 ------------------------------------------
    /**
     * 英文选义
     */
   int  ENGLISH_SELECTIONS = 0;
    /**
     * 中文选义
     */
    int CHINESE_SELECTIONS = 1;
    /**
     * 填空拼写
     */
    int WORD_SPELL = 2;

   // --------------------------------------- 单词难度 ------------------------------------------
    /**
     * 练习模式
     */
    int PRACTICE = 0;
    /**
     * 挑战模式
     */
    int CHALLENGE = 1;

    /**
     * 每道题过期时间
     */
    int EXPIRE_TIME = 60 * 60   * 3; // 3小时





}
