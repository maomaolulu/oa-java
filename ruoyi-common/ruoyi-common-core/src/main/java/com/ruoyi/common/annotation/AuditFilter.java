package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * 审批切面
 * @author zx
 */
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface AuditFilter {
    /**
     * 方法
     */
    public String method() default "";
}