package com.ruoyi.activiti.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zx
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryJpaOptimisticLock {

    /**
     * 自定义次数 默认3次
     */
    int times() default 3;
}
