package com.ruoyi.common.exception;

/**
 * 验证码错误异常类
 * 
 * @author zx
 * @date 2021-11-21 14:09:22
 */
public class CaptchaException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg)
    {
        super(msg);
    }
}
