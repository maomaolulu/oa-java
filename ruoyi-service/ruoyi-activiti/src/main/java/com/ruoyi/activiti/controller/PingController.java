package com.ruoyi.activiti.controller;

import com.google.common.collect.Maps;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysMonitor;
import com.ruoyi.system.feign.RemoteMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 监控服务是否正常
 *
 * @author zh
 * @version 1.0
 * @menu 监控服务是否正常
 */
@RestController
@RequestMapping("ping")
@Slf4j
public class PingController {
    private final RemoteMonitorService remoteMonitorService;

    public PingController(RemoteMonitorService remoteMonitorService) {
        this.remoteMonitorService = remoteMonitorService;
    }

    @GetMapping
    public R ping() {
        SysMonitor monitorInfo = remoteMonitorService.getMonitorInfo();
        HashMap<String, String> map = Maps.newHashMap();
        if (monitorInfo != null) {
            map.put("db_status", "OK");
            return R.data(map);
        } else {
            map.put("db_status", "FAILD");
            return R.data(map);
        }
    }
}
