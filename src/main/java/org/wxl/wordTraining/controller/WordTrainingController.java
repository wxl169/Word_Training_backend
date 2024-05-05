package org.wxl.wordTraining.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;

import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingJudgementDTO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingJudgementVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingTotalVO;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.WordTrainingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 单词训练控制器
 * @author wxl
 */
@RestController
@RequestMapping("/word_training")
@Slf4j
public class WordTrainingController {
    private final WordTrainingService wordTrainingService;

    @Autowired
    public WordTrainingController(WordTrainingService wordTrainingService) {
        this.wordTrainingService = wordTrainingService;
    }


    /**
     * 根据用户选择的单词类型进行单词整理
     *
     * @param wordTrainingBeginRequest 用户选择的单词训练模式
     * @param request                  获取当前登录用户信息
     * @return 单词组
     */
    @PostMapping("/begin")
    @JwtToken
    public BaseResponse<WordTrainingTotalVO> getWordList(@RequestBody WordTrainingBeginRequest wordTrainingBeginRequest, HttpServletRequest request) {
        if (wordTrainingBeginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (wordTrainingBeginRequest.getMode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择游戏模式");
        }
        if (wordTrainingBeginRequest.getDifficulty() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择游戏难度");
        }
        if (wordTrainingBeginRequest.getWordTypeList() == null || wordTrainingBeginRequest.getWordTypeList().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择单词类型");
        }
        WordTrainingTotalVO wordTrainingVO = wordTrainingService.getWordList(wordTrainingBeginRequest, request);
        return ResultUtils.success(wordTrainingVO);
    }


    /**
     * 判断答案是否正确
     * @param wordTrainingJudgementDTO 答案
     * @param request 获取登录用户
     * @return 下一题数据
     */
    @PostMapping("/judgement")
    @JwtToken
    public BaseResponse<WordTrainingJudgementVO> doJudgement(@RequestBody WordTrainingJudgementDTO wordTrainingJudgementDTO, HttpServletRequest request) {
        if (wordTrainingJudgementDTO == null || StringUtils.isBlank(wordTrainingJudgementDTO.getAnswer())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择答案");
        }
        if (wordTrainingJudgementDTO.getQuestionNumber() == null || wordTrainingJudgementDTO.getQuestionNumber() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题号错误");
        }
        if (wordTrainingJudgementDTO.getMode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择游戏模式");
        }
        if (wordTrainingJudgementDTO.getDifficulty() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先选择游戏难度");
        }
        WordTrainingJudgementVO wordTrainingJudgementVO = wordTrainingService.doJudgement(wordTrainingJudgementDTO,request);
        return ResultUtils.success(wordTrainingJudgementVO);
    }

}
