package org.wxl.wordTraining.constant;

/**
 * @author wxl
 */
public interface JWTConstant {
    /**
     * 用户储存在redis中的过期时间
     */
   long EXPIRE_TIME = 60 * 60 * 1000 * 12L;

    /**
     * 生成token的私钥
     */
    String SECRET = "maple123";

    /**
     * 前端传递token的header名称
     */
     String TOKEN_NAME = "TOKEN";

    /**
     * 用户登录token保存在redis的key值
     *
     * @param account 用户登录帐号
     * @return token保存在redis的key
     */
     static String getRedisUserKey(String account) {
        return "MAPLE_ADMIN:" + account;
    }
}
