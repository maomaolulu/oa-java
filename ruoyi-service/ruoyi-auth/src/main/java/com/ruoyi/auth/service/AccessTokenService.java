package com.ruoyi.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.model.LoginUser;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.redis.annotation.RedisEvict;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.system.domain.SysUser;

import cn.hutool.core.util.IdUtil;

import javax.servlet.http.HttpServletRequest;

@Service("accessTokenService")
@Slf4j
public class AccessTokenService
{
    private static final String WIN = "Windows";
    private static final String MAC = "Mac OS X";
    @Autowired
    private RedisUtils          redis;

    /**
     * 6小时后过期
     */
    private final static long   EXPIRE        = 6 * 60 * 60;

    private final static String ACCESS_TOKEN  = Constants.ACCESS_TOKEN;

    private final static String ACCESS_USERID = Constants.ACCESS_USERID;

    public SysUser queryByToken(String token,String type)
    {
        return redis.get(ACCESS_TOKEN + token + type, SysUser.class);
    }

    @RedisEvict(key = "user_perms", fieldKey = "#sysUser.userId")
    public Map<String, Object> createToken(SysUser sysUser,String client)
    {
        // 生成token
        String token = IdUtil.fastSimpleUUID();
        // 保存或更新用户token
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", sysUser.getUserId());
        map.put("token", token);
        map.put("expire", EXPIRE);
        // 校验是否已登录,若已经登录将踢出上一个登录的token
        HttpServletRequest request = ServletUtils.getRequest();
        final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        String deviceType = client;
//        if(os.indexOf(WIN)!=-1||MAC.equals(os)){
//            deviceType = "_pc";
//        }else{
//            deviceType = "_wx";
//        }
        expireToken(sysUser.getUserId(),deviceType);
        redis.set(ACCESS_TOKEN + token + deviceType, sysUser, EXPIRE);
        redis.set(ACCESS_USERID + sysUser.getUserId() + deviceType, token, EXPIRE);
        if("_wx".equals(deviceType)){
            log.error("ACCESS_TOKEN"+ACCESS_TOKEN + token + deviceType);
            log.error("ACCESS_USERID"+ACCESS_USERID + sysUser.getUserId() + deviceType);
        }
        return map;
    }

    public void expireToken(long userId,String type)
    {
        String token = redis.get(ACCESS_USERID + userId + type);
        if (StringUtils.isNotBlank(token))
        {
            redis.delete(ACCESS_USERID + userId + type);
            redis.delete(ACCESS_TOKEN + token + type);
        }
    }
    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
//        String token = SecurityUtils.getToken(request);
        String token = request.getHeader("token");
        return getLoginUser(token);
    }
    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token)
    {
        LoginUser user = null;
        try
        {
            if (StringUtils.isNotEmpty(token))
            {
//                String userkey = JwtUtils.getUserKey(token);
//                user = redisService.getCacheObject(getTokenKey(userkey));
                return user;
            }
        }
        catch (Exception e)
        {
        }
        return user;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
//        loginUser.setLoginTime(System.currentTimeMillis());
//        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
//        // 根据uuid将loginUser缓存
//        String userKey = getTokenKey(loginUser.getToken());
//        redisService.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }


}