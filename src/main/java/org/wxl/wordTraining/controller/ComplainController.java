package org.wxl.wordTraining.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.service.IComplainService;
import org.wxl.wordTraining.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  投诉前端控制器
 * </p>
 *
 * @author wxl
 */
@RestController
@RequestMapping("/complain")
public class ComplainController {
    private final IComplainService complainService;
    private final UserService userService;
    @Autowired
    public ComplainController(IComplainService complainService,UserService userService){
        this.complainService = complainService;
        this.userService = userService;
    }


    /**
     * 投诉
     * @param complainAddRequest 好友id
     * @param request 当前登录用户
     * @return 是否成功
     */
    @PostMapping("/add")
    public BaseResponse addComplain(@RequestBody ComplainAddRequest complainAddRequest, HttpServletRequest request){
        if (complainAddRequest== null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (complainAddRequest.getComplainId() == null || complainAddRequest.getComplainId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (complainAddRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(complainAddRequest.getComplainContent())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (complainAddRequest.getIsComplainUserId() == null || complainAddRequest.getIsComplainUserId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
      boolean add = complainService.addComplain(complainAddRequest,loginUser);
        if (add){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"投诉失败");
    }

}
