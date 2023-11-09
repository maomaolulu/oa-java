package com.ruoyi.gateway.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.exception.CaptchaException;

import java.io.IOException;

/**
 * 验证码处理
 * 
 * @author ruoyi
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public R createCapcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCapcha(String key, String value) throws CaptchaException;
}
