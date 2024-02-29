package org.wxl.wordTraining.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解 不需要验证token
 * @author wxl
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtToken {

}
