package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.model.LoginUser;
import com.ruoyi.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysUserOnlineService;

import java.util.Map;

/**
 * 在线用户 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService
{
    private final RemoteDeptService remoteDeptService;
    private final ISysLogininforService logininforService;
    @Autowired
    public SysUserOnlineServiceImpl(RemoteDeptService remoteDeptService, ISysLogininforService logininforService) {
        this.remoteDeptService = remoteDeptService;
        this.logininforService = logininforService;
    }
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user,String key)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()))
        {
            return loginUserToUserOnline(user,key);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user,String key)
    {
        if (StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user,key);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user,String key)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user,key);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public SysUserOnline loginUserToUserOnline(LoginUser user,String key)
    {
        if (StringUtils.isNull(user))
        {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(user.getSysUser().getDeptId());
        String companyName = belongCompany2.get("companyName").toString();
        sysUserOnline.setDeptName(companyName+"-"+user.getSysUser().getDept().getDeptName());
        String type = key.split("_")[3];

        SysLogininfor sysLogininfor = logininforService.selectLogininforLastByDevice(user.getUserid(),type);
        sysUserOnline.setLoginName(sysLogininfor.getLoginName());
        sysUserOnline.setLoginTime(sysLogininfor.getLoginTime());
        sysUserOnline.setIpaddr(sysLogininfor.getIpaddr());
        sysUserOnline.setLoginLocation(sysLogininfor.getLoginLocation());
        sysUserOnline.setOs(sysLogininfor.getOs());
        sysUserOnline.setBrowser(sysLogininfor.getBrowser());
        sysUserOnline.setTokenStr(key);
        sysUserOnline.setUserId(sysLogininfor.getUserId());
        sysUserOnline.setDeviceType(type);
        return sysUserOnline;
    }
}
