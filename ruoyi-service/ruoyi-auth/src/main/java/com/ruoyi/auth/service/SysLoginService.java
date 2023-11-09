package com.ruoyi.auth.service;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.user.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.log.publish.PublishFactory;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.PasswordUtil;

@Component
public class SysLoginService {

    @Autowired
    private RemoteUserService userService;

    public SysUser otherLogin(String code, String loginType, Long userId){
        // 查询用户信息
        SysUser user = userService.selectSysUserByUserId(userId);
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            PublishFactory.recordLogininfor(loginType, userId, user.getUserName(), Constants.LOGIN_FAIL,
                    MessageUtils.message("user.blocked", user.getRemark()));
            throw new UserBlockedException();
        }
        // 记录登录日志
        PublishFactory.recordLogininfor(loginType, userId, user.getLoginName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user);
        return user;
    }
    /**
     * 登录
     */
    public SysUser login(String code, String loginType, String username, String password) {
        Long userId = null;

        // 查询用户信息
        SysUser user = userService.selectSysUserByUsername(username);

        if (user.getUserId() == null) {
            PublishFactory.recordLogininfor(loginType, userId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists"));
            throw new UserNotExistsException();
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            PublishFactory.recordLogininfor(loginType, userId, username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.blocked", user.getRemark()));
            throw new UserBlockedException();
        }

        if (!PasswordUtil.matches(user, password)) {
            throw new UserPasswordNotMatchException();
        }
        userId = user.getUserId();
        // 微信被绑定不记录登录日志
        if (StrUtil.isNotBlank(code)) {
            if (null==user.getWxCode()||user.getWxCode().equals(code)) {
                // 记录登录日志
                PublishFactory.recordLogininfor(loginType, userId, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
            } else {
                PublishFactory.recordLogininfor(loginType, userId, username, Constants.LOGIN_FAIL, "非原微信");
            }

        } else {
            // 记录登录日志
            PublishFactory.recordLogininfor(loginType, userId, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        }
        recordLoginInfo(user);
        return user;
    }


    /**
     * 记录登录信息
     */
    public void recordLoginInfo(SysUser user) {
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUserLoginRecord(user);
    }

    public void logout(String loginName) {
        PublishFactory.recordLogininfor(null, null, loginName, Constants.LOGOUT, MessageUtils.message("user.logout.success"));
    }
}