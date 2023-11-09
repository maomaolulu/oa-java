package com.ruoyi.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.model.LoginUser;
import com.ruoyi.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线用户监控
 * @menu 在线用户监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisService redisService;

    /**
     * 在线用户列表
     * @param ipaddr
     * @param userName
     * @return
     */
    @HasPermissions("monitor:online:list")
    @GetMapping("/list")
    public R list(String ipaddr, String userName)
    {
        startPage();
        Collection<String> keys = redisService.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
//            Object cacheObject1 = redisService.getCacheObject(key);
            JSONObject cacheObject = redisService.getCacheObject(key);
            SysUser user1 = JSONObject.toJavaObject(cacheObject, SysUser.class);
            LoginUser user = new LoginUser();
            user.setSysUser(user1);
            user.setUserid(user1.getUserId());
            user.setIpaddr(user1.getLoginIp());
            user.setUsername(user1.getUserName());
            user.setLoginTime(user1.getLoginDate().getTime());
            user.setToken(key.split("_")[2]);

            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user,key));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user,key));
                }
            }
            else if (StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user,key));
                }
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user,key));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        List<SysUserOnline> collect = userOnlineList.stream().sorted(Comparator.comparing(SysUserOnline::getLoginTime).reversed()).collect(Collectors.toList());
        return result(collect);
    }

    /**
     * 强退用户
     * @param sysUserOnline
     */
    @HasPermissions("monitor:online:forceLogout")
    @OperLog(title = "在线用户", businessType = BusinessType.FORCE)
    @PostMapping("/force_logout")
    public R forceLogout(@RequestBody SysUserOnline sysUserOnline)
    {
        String tokenStr = sysUserOnline.getTokenStr();
        redisService.deleteObject(tokenStr);
        redisService.deleteObject( "access_userid_"+sysUserOnline.getUserId()+"_"+tokenStr.split("_")[3]);
        return R.ok("强退成功");
    }
}
