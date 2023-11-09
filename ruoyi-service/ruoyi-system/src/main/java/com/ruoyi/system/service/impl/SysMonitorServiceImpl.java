package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysMonitor;
import com.ruoyi.system.mapper.SysMonitorMapper;
import com.ruoyi.system.service.ISysMonitorService;
import org.springframework.stereotype.Service;

/**
 * @Author yrb
 * @Date 2023/5/8 8:53
 * @Version 1.0
 * @Description
 */
@Service
public class SysMonitorServiceImpl implements ISysMonitorService {

    private final SysMonitorMapper sysMonitorMapper;

    public SysMonitorServiceImpl(SysMonitorMapper sysMonitorMapper){
        this.sysMonitorMapper = sysMonitorMapper;
    }

    /**
     * 查询条数
     *
     * @return result
     */
    @Override
    public SysMonitor findInfo() {
        return sysMonitorMapper.selectOne(null);
    }
}
