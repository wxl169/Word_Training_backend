package org.wxl.wordTraining.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wxl.wordTraining.annotation.JwtToken;
import org.wxl.wordTraining.common.BaseResponse;
import org.wxl.wordTraining.constant.JWTConstant;
import org.wxl.wordTraining.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author wxl
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JwtToken annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(JwtToken.class);
        } else {
            return false;
        }
        // 如果有该注解，直接放行
        if (annotation != null) {
            log.info("有该注解放行------------------------------------");
            return true;
        }
        log.info("没有该注解,检查是否有token------------------------------------");

        // 验证token
        BaseResponse<String> verity = JwtUtil.verity();
        if (0 == verity.getCode()) {
            log.info("token验证通过------------------------------------");

            //检查该token是否过期
            String token = request.getHeader(JWTConstant.TOKEN_NAME);
            String redisKeyByToken = String.format("wordTraining:user:userLogin:%s",token);
            String userAccount = (String)redisTemplate.opsForValue().get(redisKeyByToken);
            if (userAccount == null){
                response.setStatus(401);
                return false;
            }
            String redisKeyByUserAccount = String.format("wordTraining:user:userLogin:%s",userAccount);
            String oldToken = (String)redisTemplate.opsForValue().get(redisKeyByUserAccount);
            if (oldToken == null){
                response.setStatus(401);
                return false;
            }
            redisTemplate.expire(redisKeyByToken,JWTConstant.EXPIRE_TIME, TimeUnit.MILLISECONDS);
            redisTemplate.expire(redisKeyByUserAccount,JWTConstant.EXPIRE_TIME, TimeUnit.MILLISECONDS);
            // 验证token
            return true;
        }
        // 验证不通过，返回401，表示用户未登录
        response.setStatus(401);
        return false;
    }

}
