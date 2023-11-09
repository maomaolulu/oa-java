package com.ruoyi.system.vo;


import lombok.Data;

@Data
public class PasswordForm
{
    private Long userId;
    private String    loginName;

    private String    password;

    private String    newPassword;

    private String wxCode;

    private String email;

}
