package com.ruoyi.activiti.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class CharacterConfig {
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
 
        //创建SpringWeb提供的字符编码过滤器，主要实现字符编码过滤
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        //强制对请求的编码，
        filter.setForceEncoding(true);
        //使用GBK编码
        filter.setEncoding("UTF-8");
        
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}