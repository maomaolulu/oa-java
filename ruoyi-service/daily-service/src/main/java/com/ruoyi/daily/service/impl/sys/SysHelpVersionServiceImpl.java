package com.ruoyi.daily.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.sys.SysHelpVersion;
import com.ruoyi.daily.domain.sys.dto.SysHelpVersionDTO;
import com.ruoyi.daily.mapper.sys.SysHelpVersionMapper;
import com.ruoyi.daily.service.sys.SysHelpVersionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by WuYang on 2022/8/18 10:10
 */
@Service
public class SysHelpVersionServiceImpl implements SysHelpVersionService {
    private final SysHelpVersionMapper sysHelpVersionMapper;
    @Autowired
    public SysHelpVersionServiceImpl(SysHelpVersionMapper sysHelpVersionMapper) {
        this.sysHelpVersionMapper = sysHelpVersionMapper;
    }

    @Override
    public List<SysHelpVersion> getLists(Integer type) {
        QueryWrapper<SysHelpVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",type);
        queryWrapper.orderByDesc("id");
        return sysHelpVersionMapper.selectList(queryWrapper);
    }

    @Override
    public SysHelpVersion get(Long id) {
        QueryWrapper<SysHelpVersion> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        return sysHelpVersionMapper.selectOne(wrapper);
    }

    @Override
    public void delete(Long id) {
        sysHelpVersionMapper.deleteById(id);
    }

    @Override
    public void update(SysHelpVersionDTO version) {
        SysHelpVersion sysHelpVersion = new SysHelpVersion();
        BeanUtils.copyProperties(version,sysHelpVersion);
        sysHelpVersionMapper.updateById(sysHelpVersion);
    }

    @Override
    public void add(SysHelpVersionDTO version) {
        SysHelpVersion sysHelpVersion = new SysHelpVersion();
        BeanUtils.copyProperties(version,sysHelpVersion);
        sysHelpVersionMapper.insert(sysHelpVersion);
    }
}
