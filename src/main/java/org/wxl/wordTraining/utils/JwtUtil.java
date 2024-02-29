package org.wxl.wordTraining.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.common.ErrorCode;
import org.wxl.wordTraining.common.ResultUtils;
import org.wxl.wordTraining.constant.JWTConstant;
import org.wxl.wordTraining.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jwt工具类，生成JWT和认证
 *
 * @author wxl

 */
@Slf4j
public class JwtUtil {

    private static  final  String USER_ACCOUNT = "userAccount";


    /**
     * 生成用户token,设置token超时时间
     *
     * @param userId 用户id
     * @param account 用户账号
     * @return
     */
    public static String createToken(Long userId, String account) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                // 添加头部
                .withHeader(map)
                // 放入用户的id
                .withAudience(String.valueOf(userId))
                // 可以将基本信息放到claims中
                .withClaim(USER_ACCOUNT, account)
                // 超时设置,设置过期的日期
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTConstant.EXPIRE_TIME))
                // 签发时间
                .withIssuedAt(new Date())
                // SECRET加密
                .sign(Algorithm.HMAC256(JWTConstant.SECRET));
        return token;
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        // 从请求头部中获取token信息
        String token = request.getHeader(JWTConstant.TOKEN_NAME);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRE);
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWTConstant.SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            if (null != jwt) {
                // 拿到我们放置在token中的信息
                List<String> audience = jwt.getAudience();
                if (CollectionUtil.isNotEmpty(audience)) {
                    return Long.parseLong(audience.get(0));
                }
            }
        } catch (IllegalArgumentException | JWTVerificationException e) {
            e.printStackTrace();
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR);
    }

    /**
     * 校验token并解析token
     */
    public static BaseResponse<String> verity() {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        // 从请求头部中获取token信息
        String token = request.getHeader(JWTConstant.TOKEN_NAME);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.NO_TOKEN);
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWTConstant.SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            if (null != jwt) {
                // 拿到我们放置在token中的信息
                List<String> audience = jwt.getAudience();
                if (CollectionUtil.isNotEmpty(audience)) {
                    //返回的是用户的id
                    return ResultUtils.success( audience.get(0));
//                    return ResponseResult.success("认证成功", audience.get(0));
                }
            }
        } catch (IllegalArgumentException | JWTVerificationException e) {
            e.printStackTrace();
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR);
    }
}

