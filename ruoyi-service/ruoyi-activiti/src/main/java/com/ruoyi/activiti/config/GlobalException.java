//package com.ruoyi.activiti.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
///**
// * @author wuYang
// * @date 2022/12/14 10:13
// */
//@Slf4j
//@ControllerAdvice
//public class GlobalException {
//
//
//    @ExceptionHandler({Exception.class})    //申明捕获那个异常类
//    public String ExceptionDemo(Exception e) {
//        log.error(e.getMessage(), e);
//        return "自定义异常返回:"+e.getMessage();
//    }
//
//}
