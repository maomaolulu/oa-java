package com.ruoyi.auth.form;


import lombok.Data;

@Data
public class LoginForm
{
    private String    username;

    private String    password;
    /**
     * 小程序
     */
    private String wxCode;

    private String loginType;
    /**
     * app
     */
    private String cid;
    private Long userId;
    private String email;
}
