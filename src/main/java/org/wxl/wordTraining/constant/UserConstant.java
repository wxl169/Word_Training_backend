package org.wxl.wordTraining.constant;

/**
 * 用户常量
 *
 * @author wxl
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";


    //------------------------------------账号设置----------------------------------------------
    /**
     * 账号最短长度
     */
    int ACCOUNT_MIN_LENGTH = 8;
    /**
     * 账号最长长度
     */
    int ACCOUNT_MAX_LENGTH = 10;

    /**
     * 密码最短长度
     */
    int PASSWORD_MIN_LENGTH = 8;
    /**
     * 密码最长长度
     */
    int PASSWORD_MAX_LENGTH = 16;

    /**
     * 用户默认头像
     */
     String DEFAULT_AVATAR = "https://image.cqiewxl.cn/2023/09/26/97dbdd68b48e4027988d2ce90965cdd5.jpg";

    /**
     * 每日挑战次数
     */
    int CHALLENGE_NUMBER = 5;
}
