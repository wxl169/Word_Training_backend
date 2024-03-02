package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;

/**
 * 单词训练
 * @author wxl
 */
public interface WordTrainingService {
    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的条件
     * @return 单词数据
     */
    Object getWordList(WordTrainingBeginRequest wordTrainingBeginRequest);
}
