package org.wxl.wordTraining.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.wxl.wordTraining.aop.JwtTokenInterceptor;

/**
 * @author wxl
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwt())
                .addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Bean
    public JwtTokenInterceptor jwt(){
        return new JwtTokenInterceptor();
    }
}
