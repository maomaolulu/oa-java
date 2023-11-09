package com.ruoyi.socket.service.impl;

import com.ruoyi.socket.service.UniPushService;
import com.ruoyi.socket.utils.UniPushUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * 个推接口
 * @author zx
 * @date 2022-08-16 10:00:25
 */
@Service
@EnableAsync
public class UniPushServiceImpl implements UniPushService {
    /**
     * 透传消息
     *
     * @param cid
     * @param content
     * @return
     */
    @Override
    @Async
    public void singleTransmission(String cid, String content) {
        UniPushUtil.singleTransmission(cid, content);
    }
}
