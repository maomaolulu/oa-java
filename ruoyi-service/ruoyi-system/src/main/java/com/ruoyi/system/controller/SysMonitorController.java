package com.ruoyi.system.controller;

import com.ruoyi.system.domain.SysMonitor;
import com.ruoyi.system.service.ISysMonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yrb
 * @Date 2023/5/8 8:50
 * @Version 1.0
 * @Description 监控云管家是否正常运行
 */
@RestController
@RequestMapping("/monitor")
public class SysMonitorController {
    private final ISysMonitorService sysMonitorService;

    public SysMonitorController(ISysMonitorService sysMonitorService) {
        this.sysMonitorService = sysMonitorService;
    }

    @GetMapping("/status")
    public SysMonitor find() {
        return sysMonitorService.findInfo();
    }
}
