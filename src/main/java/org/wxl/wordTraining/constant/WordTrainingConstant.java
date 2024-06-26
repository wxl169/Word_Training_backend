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


   // --------------------------------------- 存储的数据 --------
    /**
     * 是否正在练习 0：练习中，1：已结束
     */
     String IS_TRAINING_END = "0";
    /**
     * 开始时间
     */
    String BEGIN_TIME = "-1";
    /**
     * 结束时间
     */
    String END_TIME = "-2";
    /**
     * 生命值
     */
    String HEALTH_VALUE = "-3";


 /**
  * 默认生命值
  */
  int DEFAULT_HEALTH_VALUE = 3;


 /**
  * 单词组合
  */
 String[] WORD_COMBINATION = {"able", "al", "ance", "ant", "ate", "ed", "en", "er", "est", "ful", "ic", "ing",
         "ion", "ity", "ive", "less", "ly", "ment", "ness", "ous", "anti", "pre", "post", "re", "un", "ary",
         "dom", "ish", "ism", "ist", "ty", "es", "or", "us", "age", "ied", "ify", "ily", "ine", "ise", "ize",
         "let", "ure", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
         "r", "s", "t", "u", "v", "w", "x", "y", "z", "ology", "ship", "ward", "wise"
 };






}
