package com.ruoyi.daily.service.impl.sys;

import com.ruoyi.daily.domain.sys.SysHelpFaqInfo;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqInfoDTO;
import com.ruoyi.daily.mapper.sys.SysHelpFaqInfoMapper;
import com.ruoyi.daily.service.sys.SysHelpFaqInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by WuYang on 2022/8/18 10:10
 */
@Service
public class SysHelpFaqInfoServiceImpl implements SysHelpFaqInfoService {
    private final SysHelpFaqInfoMapper sysHelpFaqInfoMapper;
    @Autowired
    public SysHelpFaqInfoServiceImpl(SysHelpFaqInfoMapper sysHelpFaqInfoMapper) {
        this.sysHelpFaqInfoMapper = sysHelpFaqInfoMapper;
    }

    @Override
    public void add(SysHelpFaqInfo info) {
        sysHelpFaqInfoMapper.insert(info);
    }

    @Override
    public void editAdd(SysHelpFaqInfoDTO dto) {
        SysHelpFaqInfo info = new SysHelpFaqInfo();
        BeanUtils.copyProperties(dto,info);
        sysHelpFaqInfoMapper.insert(info);
    }
}
