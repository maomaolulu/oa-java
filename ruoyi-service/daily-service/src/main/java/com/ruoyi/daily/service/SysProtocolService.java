package com.ruoyi.daily.service;

import com.ruoyi.daily.domain.SysProtocol;

/**
 * @author zx
 * @date 2022/1/12 13:53
 */
public interface SysProtocolService {
    /**
     * 获取协议
     * @param type 协议类型
     * @return
     */
    SysProtocol get(String type);
}
