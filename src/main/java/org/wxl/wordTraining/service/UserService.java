package org.wxl.wordTraining.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.wordTraining.model.dto.user.UserListRequest;
import org.wxl.wordTraining.model.dto.user.UserUpdateByUserRequest;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.user.LoginUserVO;
import org.wxl.wordTraining.model.vo.user.UserListVO;
import org.wxl.wordTraining.model.vo.user.UserPointRankVO;
import org.wxl.wordTraining.model.vo.user.UserVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author wxl
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword );

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);


    /**
     * 分页获取用户封装列表
     *
     * @param userListRequest 分页筛选条件
     * @return 脱敏用户列表数据
     */
    PageVO getUserList(UserListRequest userListRequest);

    /**
     * 关注好友
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 是否成功
     */
    boolean addFriend(Long friendId, User loginUser);
    /**
     * 取关好友
     *
     * @param friendId 好友id
     * @param loginUser   当前登录用户
     * @return 是否成功
     */
    boolean deleteFriend(Long friendId, User loginUser);

    /**
     * 用户更新自己的信息
     *
     * @param userUpdateByUserRequest 用户需要修改的数据
     * @param request 当前用户信息
     * @return 是否修改成功
     */
    boolean updateUser(UserUpdateByUserRequest userUpdateByUserRequest, HttpServletRequest request);


    /**
     * 获取积分排行榜信息
     * @return 积分排行榜信息
     */
    List<UserPointRankVO> getPointsRanking();
}
