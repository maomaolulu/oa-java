package com.ruoyi.system.feign;

import com.ruoyi.common.constant.ServiceNameConstants;
import com.ruoyi.system.domain.SysMonitor;
import com.ruoyi.system.feign.factory.RemoteMonitorFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author yrb
 * @Date 2023/5/10 15:23
 * @Version 1.0
 * @Description
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMonitorFallback.class)
public interface RemoteMonitorService {

    @GetMapping("/monitor/status")
    SysMonitor getMonitorInfo();
}
