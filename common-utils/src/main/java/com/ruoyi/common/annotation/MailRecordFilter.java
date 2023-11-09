package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * 邮件记录切面
 * @author zx
 */
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface MailRecordFilter {
    /**
     * 方法
     */
    public String method() default "";
}