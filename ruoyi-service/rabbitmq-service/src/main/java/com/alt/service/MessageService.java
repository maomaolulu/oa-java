package com.alt.service;

import com.alt.entity.dto.MessageDto;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息管理
 * @author zx
 * @date 2022-09-06 19:52:31
 */
public interface MessageService {
    /**
     * 发送消息
     * @param message 消息实体
     */
    void save(MessageDto message) throws IOException, TimeoutException;
}
