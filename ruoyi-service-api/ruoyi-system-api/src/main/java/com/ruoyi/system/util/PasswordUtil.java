package com.ruoyi.system.util;

import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.system.domain.SysUser;

public class PasswordUtil
{
    public static boolean matches(SysUser user, String newPassword)
    {
        String s = encryptPassword(user.getLoginName(), newPassword, user.getSalt());
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    public static String encryptPassword(String username, String password, String salt)
    {
        return Md5Utils.hash(username + password + salt);
    }

    public static void main(String[] args) {
        System.out.println(encryptPassword("zhangsan","123456","1"));
    }
}