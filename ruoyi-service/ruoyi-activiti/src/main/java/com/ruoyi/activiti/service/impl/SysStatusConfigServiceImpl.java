package com.ruoyi.activiti.service.impl;

import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.SysStatusConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 静态文件管理表
 *
 * @author zh
 * @date 2021-12-31
 */
@Service
@Slf4j
public class SysStatusConfigServiceImpl implements SysStatusConfigService {
    private final SysStatusConfigMapper sysStatusConfigMapper;

    @Autowired
    public SysStatusConfigServiceImpl(SysStatusConfigMapper sysStatusConfigMapper) {
        this.sysStatusConfigMapper = sysStatusConfigMapper;

    }

    @Override
    public SysStatusConfig selectFielPathName() {
        List<SysStatusConfig> sysStatusConfigs = sysStatusConfigMapper.selectList(null);
        return sysStatusConfigs.get(0);
    }

    @Override
    public SysStatusConfig updateById(SysStatusConfig sysStatusConfig) {
        sysStatusConfigMapper.updateById(sysStatusConfig);
        return sysStatusConfig;
    }
}
