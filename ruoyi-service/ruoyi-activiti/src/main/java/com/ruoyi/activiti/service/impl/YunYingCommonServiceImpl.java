package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.properties.YunYingProperties;
import com.ruoyi.activiti.service.YunYingCommonService;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/6/14 14:46
 * @Version 1.0
 * @Description 运营系统公共service
 */
@Service
@Slf4j
public class YunYingCommonServiceImpl implements YunYingCommonService {
    private final RedisUtils redisUtil;
    private final RemoteUserService remoteUserService;
    private final YunYingProperties yunYingProperties;
    private final RemoteConfigService remoteConfigService;

    public YunYingCommonServiceImpl(RedisUtils redisUtil,
                                    RemoteUserService remoteUserService,
                                    YunYingProperties yunYingProperties,
                                    RemoteConfigService remoteConfigService) {
        this.redisUtil = redisUtil;
        this.remoteUserService = remoteUserService;
        this.yunYingProperties = yunYingProperties;
        this.remoteConfigService = remoteConfigService;
    }

    /**
     * 获取token
     *
     * @param processKey 流程key
     * @return token
     */
    @Override
    public String getToken(String processKey) {
        String key = "yunying-(" + processKey + ")-" + SystemUtil.getUserId() + "-token";
        String token = redisUtil.get(key);
        SysConfig configUrl = remoteConfigService.findConfigUrl();
        if (StrUtil.isBlank(token)) {
            // 获取用户信息
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            String email = sysUser.getEmail();
            if (StrUtil.isBlank(email)) {
                log.error("未获取到用户email,请联系管理员");
                return "";
            }
            // 获取秘钥
            String secretKey = yunYingProperties.getSecretKey();
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("email", email);
            paramMap.put("secretKey", secretKey);
            paramMap.put("timeStamp", System.currentTimeMillis());
            paramMap.put("isWant", false);
            String response;
            if ("test".equals(configUrl.getConfigValue())) {
                response = HttpUtil.post(UrlConstants.YY_TOKEN_TEST, JSON.toJSONString(paramMap));
            } else {
                response = HttpUtil.post(UrlConstants.YY_TOKEN_ONLINE, JSON.toJSONString(paramMap));
            }
            Map<String, Object> map = JSON.parseObject(response);
            Integer code = (Integer) map.get("code");
            if (200 != code) {
                log.error("用户" + SystemUtil.getUserNameCn() + "获取token失败:" + response);
                return "";
            }
            JSONObject data = JSON.parseObject(String.valueOf(map.get("data")));
            token = String.valueOf(data.get("token"));
            redisUtil.set(key, data.get("token"), 1740);
        }
        return token;
    }
}
