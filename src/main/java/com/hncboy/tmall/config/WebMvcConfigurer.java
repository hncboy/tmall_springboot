package com.hncboy.tmall.config;

import com.hncboy.tmall.interceptor.LoginInterceptor;
import com.hncboy.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/22
 * Time: 12:23
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public OtherInterceptor getOtherInterceptor() {
        return new OtherInterceptor();
    }

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getOtherInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**");
    }
}
