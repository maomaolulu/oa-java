package com.ruoyi.daily.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.SysProtocol;
import com.ruoyi.daily.mapper.SysProtocolMapper;
import com.ruoyi.daily.service.SysProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zx
 * @date 2022/1/12 13:53
 */
@Service
public class SysProtocolServiceImpl implements SysProtocolService {
    private final SysProtocolMapper protocolMapper;
    @Autowired
    public SysProtocolServiceImpl(SysProtocolMapper protocolMapper) {
        this.protocolMapper = protocolMapper;
    }

    /**
     * 获取协议
     *
     * @param type 协议类型
     * @return
     */
    @Override
    public SysProtocol get(String type) {
        QueryWrapper<SysProtocol> wrapper = new QueryWrapper<>();
        wrapper.eq("types",type);
        wrapper.eq("is_active","1");
        return protocolMapper.selectOne(wrapper);
    }
}
