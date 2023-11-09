package com.ruoyi.daily.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.sys.SysHelpHot;
import com.ruoyi.daily.domain.sys.dto.SysHelpHotDTO;
import com.ruoyi.daily.mapper.sys.SysHelpHotMapper;
import com.ruoyi.daily.service.sys.SysHelpHotService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热点问题管理
 * Created by WuYang on 2022/8/18 10:10
 */
@Service
public class SysHelpHotServiceImpl implements SysHelpHotService {
    private final SysHelpHotMapper sysHelpHotMapper;
    @Autowired
    public SysHelpHotServiceImpl(SysHelpHotMapper sysHelpHotMapper) {
        this.sysHelpHotMapper = sysHelpHotMapper;
    }

    @Override
    public List<SysHelpHot> getLists() {
        QueryWrapper<SysHelpHot> queryWrapper = new QueryWrapper<>();
        return sysHelpHotMapper.selectList(queryWrapper);
    }

    @Override
    public SysHelpHot get(Long id) {
        return sysHelpHotMapper.selectById(id);

    }

    @Override
    public void delete(Long id) {
        sysHelpHotMapper.deleteById(id);
    }

    @Override
    public void update(SysHelpHotDTO hotDTO) {
        SysHelpHot sysHelpHot = new SysHelpHot();
        BeanUtils.copyProperties(hotDTO, sysHelpHot);
        sysHelpHotMapper.updateById(sysHelpHot);
    }

    @Override
    public void add(SysHelpHotDTO hotDTO) {
        SysHelpHot sysHelpHot = new SysHelpHot();
        BeanUtils.copyProperties(hotDTO, sysHelpHot);
        sysHelpHotMapper.insert(sysHelpHot);
    }
}
