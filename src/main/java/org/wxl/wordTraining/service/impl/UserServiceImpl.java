package org.wxl.wordTraining.service.impl;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.constant.CommonConstant;
import org.wxl.wordTraining.constant.JWTConstant;
import org.wxl.wordTraining.constant.PraiseConstant;
import org.wxl.wordTraining.exception.BusinessException;
import org.wxl.wordTraining.exception.ThrowUtils;
import org.wxl.wordTraining.mapper.UserMapper;
import org.wxl.wordTraining.model.dto.user.UserListRequest;
import org.wxl.wordTraining.model.entity.Praise;
import org.wxl.wordTraining.model.entity.User;
import org.wxl.wordTraining.model.enums.UserRoleEnum;
import org.wxl.wordTraining.model.vo.PageVO;
import org.wxl.wordTraining.model.vo.user.LoginUserVO;
import org.wxl.wordTraining.model.vo.user.UserListVO;
import org.wxl.wordTraining.model.vo.user.UserVO;
import org.wxl.wordTraining.service.UserService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.wxl.wordTraining.constant.UserConstant;
import org.wxl.wordTraining.utils.BeanCopyUtils;
import org.wxl.wordTraining.utils.JwtUtil;

/**
 * 用户服务实现
 *
 * @author wxl
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Random random = new Random();
    private final RedissonClient redissonClient;
    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String,Object> redisTemplate,RedissonClient redissonClient) {
      this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.redissonClient =redissonClient;
    }

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "wxl";

    /**
     * 注册用户
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 注册后的主键id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        this.judgeUserAccountAndPassword(userAccount,userPassword);

        if(checkPassword.length() < UserConstant.PASSWORD_MIN_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (checkPassword.length() > UserConstant.PASSWORD_MAX_LENGTH ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        synchronized (userAccount.intern()) {
            //账号不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount,userAccount);

            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }

            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUsername("用户"+userAccount);
            user.setGender(0);
            user.setAvatar(UserConstant.DEFAULT_AVATAR);
            user.setRole(UserConstant.DEFAULT_ROLE);
            user.setPointNumber(0L);
            user.setCoiledDay(0L);
            user.setOnlineDay(0L);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setIsDelete(0);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 登录账号
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        if (userAccount.length() < UserConstant.ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        queryWrapper.eq(User::getUserPassword,encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        User newUser = null;
        synchronized (userAccount.intern()){
            //修改最近登录时间，并记录在线天数
            newUser = this.addLoginTime(user);
        }

        String redisKeyByUserAccount = String.format("wordTraining:user:userLogin:%s",userAccount);

        //根据账号查询Redis判断该账号是否登录
        String  oldTokenByUserAccount = (String) redisTemplate.opsForValue().get(redisKeyByUserAccount);
        log.info("oldToken为：{}",oldTokenByUserAccount);

        String redisKeyByToken = null;
        if (oldTokenByUserAccount != null){
            //如果已经登录，则删除原token
            redisKeyByToken = String.format("wordTraining:user:userLogin:%s",oldTokenByUserAccount);
            redisTemplate.delete(redisKeyByToken);
            redisTemplate.delete(redisKeyByUserAccount);
        }

        //生成token，并返回脱敏后的用户数据
        LoginUserVO loginUserVO = this.getLoginUserVO(newUser);
        String newToken = JwtUtil.createToken(user.getId(), user.getUserAccount());
        loginUserVO.setToken(newToken);
        int randomMinutes = random.nextInt(10) + 1;
        int totalMinutes = 60 * 12 + randomMinutes;
        redisKeyByToken = String.format("wordTraining:user:userLogin:%s",newToken);
        redisTemplate.opsForValue().set(redisKeyByToken,userAccount,totalMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(redisKeyByUserAccount,newToken,totalMinutes, TimeUnit.MINUTES);
        return loginUserVO;
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(JWTConstant.TOKEN_NAME);
        if (token == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String redisKeyByToken  = String.format("wordTraining:user:userLogin:%s",token);
        String userAccount = (String) redisTemplate.opsForValue().get(redisKeyByToken);
        if (userAccount == null){
            throw new BusinessException(ErrorCode.TOKEN_EXPIRE);
        }
        String redisKeyByUserAccount = String.format("wordTraining:user:userLogin:%s",userAccount);
        String oldToken = (String) redisTemplate.opsForValue().get(redisKeyByUserAccount);
        if (oldToken == null) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRE);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User currentUser = userMapper.selectOne(queryWrapper);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        String token = request.getHeader(JWTConstant.TOKEN_NAME);
        if (token == null){
            return  null;
        }
        String redisKeyByToken  = String.format("wordTraining:user:userLogin:%s",token);
        String userAccount = (String) redisTemplate.opsForValue().get(redisKeyByToken);
        if (userAccount == null){
            return null;
        }
        String redisKeyByUserAccount = String.format("wordTraining:user:userLogin:%s",userAccount);
        String oldToken = (String) redisTemplate.opsForValue().get(redisKeyByUserAccount);
        if (oldToken == null) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User currentUser = userMapper.selectOne(queryWrapper);
        return currentUser;
    }





    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        String token = request.getHeader(JWTConstant.TOKEN_NAME);
        if (token == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String redisKeyByToken  = String.format("wordTraining:user:userLogin:%s",token);
        String userAccount = (String) redisTemplate.opsForValue().get(redisKeyByToken);
        if (userAccount == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String redisKeyByUserAccount = String.format("wordTraining:user:userLogin:%s",userAccount);
//        String oldToken = (String)redisTemplate.opsForValue().get(redisKeyByUserAccount);
//        if (oldToken == null){
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        redisTemplate.delete(redisKeyByToken);
        redisTemplate.delete(redisKeyByUserAccount);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 分页获取用户封装列表
     * @param userListRequest 分页筛选条件
     * @return 脱敏用户数据列表
     */
    @Override
    public PageVO getUserList(UserListRequest userListRequest) {
        Integer current = userListRequest.getCurrent();
        Integer pageSize = userListRequest.getPageSize();

        if (current == null || current <= 0){
            current = CommonConstant.PAGE_NUM;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = CommonConstant.PAGE_SIZE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(userListRequest.getUserAccount()),User::getUserAccount,userListRequest.getUserAccount())
                .like(StringUtils.isNotBlank(userListRequest.getUsername()),User::getUsername,userListRequest.getUsername())
                .eq(userListRequest.getGender() != null,User::getGender,userListRequest.getGender())
                .eq(StringUtils.isNotBlank(userListRequest.getRole()),User::getRole,userListRequest.getRole());
        Page<User> page = new Page<>(current,pageSize);
        page(page,queryWrapper);
        List<UserListVO> userListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserListVO.class);
        return new PageVO(userListVOS,page.getTotal());
    }

    /**
     * 关注好友
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 是否成功关注
      */
    @Override
    public boolean addFriend(Long friendId, User loginUser) {
        RLock userLock = redissonClient.getLock("wordTraining:user:addFriend:user:" + loginUser.getId());
        Gson gson = new Gson();
        try {
            while (true){
                boolean tryLock = userLock.tryLock(0,-1, TimeUnit.SECONDS);
                if (tryLock){
                    //判断当前用户是否关注该用户
                    Set<Long> friendList = gson.fromJson(loginUser.getConcern(), new TypeToken<Set<Long>>() {
                    }.getType());
                    if (friendList != null && friendList.contains(friendId)){
                        //如果已经成功关注用户
                        throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复关注该用户");
                    }
                    if (friendList == null){
                        friendList = new HashSet<>();
                    }
                    //关注该用户
                    friendList.add(friendId);
                    String concern = gson.toJson(friendList);
                    LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(User::getId,loginUser.getId())
                            .set(User::getConcern,concern);
                    boolean update = this.update(updateWrapper);
                    if (!update){
                        throw new BusinessException(ErrorCode.OPERATION_ERROR,"关注该用户失败");
                    }
                    return true;
                }
            }
        } catch (InterruptedException e) {
            log.error("doCollection error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"关注失败");
        } finally {
            userLock.unlock();
        }
    }

    /**
     * 取关好友
     * @param friendId 好友id
     * @param loginUser   当前登录用户
     * @return 是否成功
     */
    @Override
    public boolean deleteFriend(Long friendId, User loginUser) {
        Gson gson = new Gson();
        //判断当前用户是否存在该好友
        Set<Long> friendList = gson.fromJson(loginUser.getConcern(), new TypeToken<Set<Long>>() {
        }.getType());
        if (friendList != null  && friendList.contains(friendId)){
            //取关该用户
            friendList.remove(friendId);
            String concern = gson.toJson(friendList);
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId,loginUser.getId())
                    .set(User::getConcern,concern);
            boolean update = this.update(updateWrapper);
            if (!update){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"取关该用户失败");
            }
            return true;
        }
        return false;
    }


    /**
     * 判断账号密码格式是否正确
     * @param userAccount 账号
     * @param userPassword 密码
     */
    private void judgeUserAccountAndPassword(String userAccount,String userPassword){
        if (userAccount.length() < UserConstant.ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userAccount.length() > UserConstant.ACCOUNT_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过长");
        }
        if (userPassword.length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (userPassword.length() > UserConstant.PASSWORD_MAX_LENGTH ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
    }


    /**
     * 判断两个时间是否在同一天
     * @param date1 当前时间
     * @param date2 数据库中的时间
     * @return 是否在同一天
     */
    public boolean isSameDay(LocalDate date1, LocalDate date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date1.format(formatter).equals(date2.format(formatter));
    }


    /**
     * 修改在线天数
     * @param user 旧用户信息
     * @return  新用户信息
     */
    private User addLoginTime(User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        if (user.getLastLoginTime() == null && user.getOnlineDay() == 0){
            updateWrapper.set(User::getLastLoginTime,LocalDateTime.now())
                    .set(User::getCoiledDay,1)
                    .set(User::getOnlineDay,1)
                    .eq(User::getId,user.getId());
        }else if(user.getLastLoginTime() == null && user.getOnlineDay() != 0){
            updateWrapper.set(User::getLastLoginTime,LocalDateTime.now())
                    .set(User::getCoiledDay,1)
                    .set(User::getOnlineDay,user.getOnlineDay() + 1)
                    .eq(User::getId,user.getId());
        } else {
            //记录当前登录用户最近登录时间
            updateWrapper.set(!this.isSameDay(LocalDate.from(user.getLastLoginTime()), LocalDate.from(LocalDateTime.now())), User::getOnlineDay, user.getOnlineDay() + 1)
                    .set(User::getLastLoginTime, LocalDateTime.now())
                    .eq(User::getId, user.getId());
            //判断用户是否连续登录
            if(LocalDateTime.now().getDayOfMonth() - user.getLastLoginTime().getDayOfMonth() == 1){
                //有则修改为1
                updateWrapper.set(User::getCoiledDay,1);
            }else{
                //没有则加1
                updateWrapper.set(!this.isSameDay(LocalDate.from(user.getLastLoginTime()), LocalDate.from(LocalDateTime.now())),User::getCoiledDay,user.getCoiledDay() + 1);
            }
        }
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请重新登录");
        }
        log.info("旧用户信息{}",user);
        User newUser = userMapper.selectById(user.getId());
        log.info("新用户信息{}",newUser);
        return newUser;
    }
}
