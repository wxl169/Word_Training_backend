package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 单词训练
 * @author wxl
 */
public interface WordTrainingService {
    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param request 请求
     * @return 单词数据
     */
   WordTrainingVO getWordList(WordTrainingBeginRequest wordTrainingBeginRequest,HttpServletRequest request);
}
