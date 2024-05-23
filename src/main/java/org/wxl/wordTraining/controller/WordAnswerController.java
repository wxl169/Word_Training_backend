package org.wxl.wordTraining.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.wordAnswer.WordAnswerListRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.service.IWordAnswerService;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  单词答题记录前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/word_answer")
public class WordAnswerController {
        private IWordAnswerService wordAnswerService;
        private final UserService userService;
        @Autowired
        public WordAnswerController(IWordAnswerService wordAnswerService,UserService userService) {
            this.wordAnswerService = wordAnswerService;
            this.userService = userService;
        }

    /**
     * 根据条件获取单词答题记录
     * @param wordAnswerListRequest 搜索条件
     * @param request 获取当前登录用户信息
     * @return 单词答题记录
     */
       @PostMapping("/get")
        public BaseResponse<PageVO> getWordAnswerList(@RequestBody WordAnswerListRequest wordAnswerListRequest, HttpServletRequest request) {
        if (wordAnswerListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        PageVO pageVO = wordAnswerService.getWordAnswerList(wordAnswerListRequest,loginUser);
        return ResultUtils.success(pageVO);
        }


}
