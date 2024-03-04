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
import org.wxl.wordTraining.model.dto.collection.CollectionRequest;
import org.wxl.wordTraining.model.dto.praise.PraiseAddRequest;
import org.wxl.wordTraining.model.dto.praise.PraiseDeleteRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.ICollectionService;
import org.wxl.wordTraining.service.IPraiseService;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  点赞前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/praise")
public class PraiseController {
    private final IPraiseService praiseService;
    private final UserService userService;
    @Autowired
    public PraiseController(IPraiseService praiseService,UserService userService){
        this.praiseService = praiseService;
        this.userService = userService;
    }

    /**
     * 当前用户点赞单词
     * @param praiseAddRequest 点赞物id和类型（0：文章，1：评论）
     * @param request 获取当前登录信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addPraise(@RequestBody PraiseAddRequest praiseAddRequest, HttpServletRequest request){
        if (praiseAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (praiseAddRequest.getId() == null || praiseAddRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选中点赞物");
        }
        if (praiseAddRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请注明点赞物类型");
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(praiseService.addPraise(praiseAddRequest,loginUser));
    }

    /**
     * 当前用户取消点赞单词
     * @param praiseDeleteRequest 点赞物id和类型（0：文章，1：评论）
     * @param request 获取当前登录信息
     * @return 是否取消成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean>  deletePraise(@RequestBody PraiseDeleteRequest praiseDeleteRequest, HttpServletRequest request){
        if (praiseDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (praiseDeleteRequest.getId() == null || praiseDeleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选中点赞物");
        }
        if (praiseDeleteRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请注明点赞物类型");
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(praiseService.deletePraise(praiseDeleteRequest,loginUser));
    }
}
