package com.ruoyi.system.feign;

import com.ruoyi.common.constant.ServiceNameConstants;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.feign.factory.RemoteConfigFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteConfigFallback.class)
public interface RemoteConfigService
{

    /**
     * 查询参数配置列表
     */
    @PostMapping("config/list/operating")
    List<SysConfig> listOperating(@RequestBody SysConfig sysConfig);

    @GetMapping("/config/url")
    SysConfig findConfigUrl();
}
