package org.wxl.wordTraining.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wxl.wordTraining.annotation.AuthCheck;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.*;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.model.dto.user.*;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.user.LoginUserVO;
import org.wxl.wordTraining.model.vo.user.UserPointRankVO;
import org.wxl.wordTraining.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.wxl.wordTraining.utils.RegularUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用户接口
 *
 * @author wxl
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @JwtToken
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 登录用户的账号密码
     * @return 当前登录用户的脱敏信息
     */
    @PostMapping("/login")
    @JwtToken
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword);
        return ResultUtils.success(loginUserVO);
    }



    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        if (!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success("退出成功");
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }


    /**
     * 删除用户
     *
     * @param deleteRequest 用户id
     * @param request 用户信息
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户需要修改的数据
     * @param request 当前用户信息
     * @return 是否修改成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 用户更新自己的信息
     *
     * @param userUpdateByUserRequest 用户需要修改的数据
     * @param request 当前用户信息
     * @return 是否修改成功
     */
    @PostMapping("/update/all")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateByUserRequest userUpdateByUserRequest,
                                            HttpServletRequest request) {
        if (userUpdateByUserRequest == null || userUpdateByUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update  = userService.updateUser(userUpdateByUserRequest,request);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR,"修改信息失败");
        return ResultUtils.success(true);
    }




    /**
     * 分页获取用户封装列表
     *
     * @param userListRequest 分页筛选条件
     * @param request 获取登录信息
     * @return 脱敏用户列表数据
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO> listUserVOByPage(@RequestBody UserListRequest userListRequest,
                                                 HttpServletRequest request) {
        if (userListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageVO userList = userService.getUserList(userListRequest);
        return ResultUtils.success(userList);

    }


    /**
     * 关注好友
     @param idRequest 好友id
      * @param request 当前登录用户
     * @return 是否成功
     */
    @PostMapping("/add/friend")
    public BaseResponse addFriend(@RequestBody IdRequest idRequest,HttpServletRequest request){
        if (idRequest== null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = userService.addFriend(idRequest.getId(),loginUser);
        if (add){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"关注好友失败");
    }

    /**
     * 取关好友
     *
     * @param idRequest 好友id
     * @param request   当前登录用户
     * @return 是否成功
     */
    @PostMapping("/delete/friend")
    public BaseResponse deleteFriend(@RequestBody IdRequest idRequest, HttpServletRequest request){
        if (idRequest== null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean add = userService.deleteFriend(idRequest.getId(),loginUser);
        if (add){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"关注好友失败");
    }


    /**
     * 获取积分排行榜
     *
     * @return 积分排行
     */
    @PostMapping("/get/rank")
    @JwtToken
    public BaseResponse<List<UserPointRankVO>> getPointsRanking() {
        return ResultUtils.success(userService.getPointsRanking());
    }



}
