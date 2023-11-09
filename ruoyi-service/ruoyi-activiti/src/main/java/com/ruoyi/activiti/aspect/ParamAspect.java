package com.ruoyi.activiti.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wuYang
 * @date 2023/3/7 9:03
 */
@Aspect
@Component
@Order(1)
public class ParamAspect {
    @Pointcut("@annotation(com.ruoyi.activiti.annotation.RequestParamMongo)")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("参数" + (i + 1) + ":" + args[i]);
            }
        }
    }
}
