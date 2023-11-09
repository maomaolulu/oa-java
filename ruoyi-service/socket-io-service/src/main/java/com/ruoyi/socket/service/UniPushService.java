package com.ruoyi.socket.service;

public interface UniPushService {
    /**
     * 透传消息
     * @param cid
     * @param content
     * @return
     */
    void singleTransmission(String cid, String content);
}
