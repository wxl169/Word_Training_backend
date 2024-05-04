package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingJudgementDTO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingJudgementVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingTotalVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;

import javax.servlet.http.HttpServletRequest;

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
    WordTrainingTotalVO getWordList(WordTrainingBeginRequest wordTrainingBeginRequest, HttpServletRequest request);

    /**
     * 判断答案是否正确
     * @param wordTrainingJudgementDTO 答案
     * @param request 获取登录用户信息
     * @return 判断结果
     */
    WordTrainingJudgementVO doJudgement(WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request);
}
