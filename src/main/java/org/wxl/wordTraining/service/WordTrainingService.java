package org.wxl.wordTraining.service;

import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingEndDTO;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingJudgementDTO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingEndVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingJudgementVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingTotalVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * 单词训练
 * @author wxl
 */
public interface WordTrainingService {
    /**
     * 根据用户选择的单词类型进行单词整理
     *
     * @param wordTrainingBeginRequest 用户选择的条件
     * @param request 请求
     * @return 单词数据
     */
    WordTrainingTotalVO getWordList(WordTrainingBeginRequest wordTrainingBeginRequest, HttpServletRequest request);

    /**
     * 判断答案是否正确
     *
     * @param wordTrainingJudgementDTO 答案
     * @param request 获取登录用户信息
     * @return 判断结果
     */
    WordTrainingJudgementVO doJudgement(WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request);
    /**
     * 结算训练结果
     *
     * @param wordTrainingEndDTO 当前训练信息
     * @param request 获取当前登录用户
     * @return 训练结果信息
     */
    WordTrainingEndVO balanceTraining(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request) throws ParseException;

    /**
     * 结算单词训练
     *
     * @param wordTrainingEndDTO 结算信息
     * @param request 获取当前登录用户
     * @return 结算结果
     */
    Boolean settlementWordTraining(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request);

    /**
     * 在训练过程中结束训练
     *
     * @param wordTrainingEndDTO 训练信息
     * @param request 获取当前登录用户
     * @return 是否结束成功
     */
    Boolean endTrainingInBegin(WordTrainingEndDTO wordTrainingEndDTO, HttpServletRequest request);

    /**
     * 单词训练时间结束
     *
     * @param wordTrainingJudgementDTO 答案
     * @param request    获取登录用户信息
     * @return 判断结果
     */
    WordTrainingJudgementVO wordTrainingTimeEnd(WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request);
}
