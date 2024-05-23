package org.wxl.wordTraining.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.IdRequest;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.model.dto.complain.ComplainAddRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainAditingRequest;
import org.wxl.wordTraining.model.dto.complain.ComplainListRequest;
import org.wxl.wordTraining.model.dto.word.wordType.WordTypeListRequest;
import org.wxl.wordTraining.model.entity.Complain;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.complain.ComplainOneVO;
import org.wxl.wordTraining.service.IComplainService;
import org.wxl.wordTraining.service.UserService;
import org.wxl.wordTraining.utils.BeanCopyUtils;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择投诉对象");
        }
        if (complainAddRequest.getType() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(complainAddRequest.getComplainContent())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入投诉内容");
        }
        if (complainAddRequest.getIsComplainUserId() == null || complainAddRequest.getIsComplainUserId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择投诉人");
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = complainService.addComplain(complainAddRequest,loginUser);
        if (add){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"投诉失败");
    }


    /**
     * 根据条件查询投诉列表
     * @param complainListRequest 条件
     * @param request 当前登录用户
     * @return 投诉列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO> listComplainVOByPage(@RequestBody ComplainListRequest complainListRequest,
                                                     HttpServletRequest request) {
        if (complainListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageVO wordTypeList = complainService.getComplainListByPage(complainListRequest,request);
        return ResultUtils.success(wordTypeList);
    }


    @PostMapping("/aditing")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateComplainAditing(@RequestBody ComplainAditingRequest complainAditingRequest,
                                                     HttpServletRequest request) {
        if (complainAditingRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (complainAditingRequest.getId() == null || complainAditingRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择投诉对象");
        }
        if(StringUtils.isBlank(complainAditingRequest.getReviewComment())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入审核内容");
        }
        Boolean wordTypeList = complainService.updateComplainAditing(complainAditingRequest,request);
        return ResultUtils.success(wordTypeList);
    }

    /**
     * 根据Id获取投诉信息
     * @param idRequest 投诉id
     * @param request 当前登录用户
     * @return 投诉信息
     */
    @PostMapping("/get/one")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<ComplainOneVO> selectComplainById(@RequestBody IdRequest idRequest, HttpServletRequest request){
        if (idRequest == null || idRequest.getId() == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Complain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Complain::getId,idRequest.getId());
        Complain complain = complainService.getOne(queryWrapper);
        ComplainOneVO complainOneVO = BeanCopyUtils.copyBean(complain, ComplainOneVO.class);
        return ResultUtils.success(complainOneVO);
    }
}
