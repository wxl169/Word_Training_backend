package org.wxl.wordTraining.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.wordTraining.WordTrainingBeginRequest;
import org.wxl.wordTraining.model.vo.wordTraining.WordTrainingVO;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.service.WordTrainingService;

import java.util.List;

/**
 * 单词训练控制器
 * @author wxl
 */
@RestController
@RequestMapping("/word_training")
@Slf4j
public class WordTrainingController {
    private final UserService userService;
    private final WordTrainingService wordTrainingService;
    @Autowired
    public WordTrainingController(UserService userService,WordTrainingService wordTrainingService) {
        this.userService = userService;
        this.wordTrainingService = wordTrainingService;
    }


    /**
     * 根据用户选择的单词类型进行单词整理
     * @param wordTrainingBeginRequest 用户选择的单词训练模式
     * @return 单词组
     */
    @GetMapping("/get")
    public BaseResponse getWordList(@RequestBody WordTrainingBeginRequest wordTrainingBeginRequest){
        if (wordTrainingBeginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (wordTrainingBeginRequest.getMode() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请先选择游戏模式");
        }
        if (wordTrainingBeginRequest.getDifficulty() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请先选择游戏难度");
        }
        if (wordTrainingBeginRequest.getWordTypeList() == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请先选择单词类型");
        }
        Object wordTrainingVOList =  wordTrainingService.getWordList(wordTrainingBeginRequest);
        return ResultUtils.success(wordTrainingVOList);
    }



}
