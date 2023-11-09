package com.ruoyi.activiti.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Configuration
public class LongToString implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        //json中多余的参数不报错，不想要可以改掉
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置全局的时间转化
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(smt);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //设置中文编码格式
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        jackson2HttpMessageConverter.setSupportedMediaTypes(list);

        //不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        //放到第一个
        converters.add(0, jackson2HttpMessageConverter);
    }
}