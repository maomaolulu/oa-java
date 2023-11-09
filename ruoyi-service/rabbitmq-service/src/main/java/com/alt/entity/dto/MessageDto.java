package com.alt.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zx
 * @date 2022/9/6 17:20
 * @description MessageDto
 */
@Data
public class MessageDto {
    /**
     * 发送者id
     */
    private String senderId;
    /**
     * 消息来源
     */
    private String source;
    /**
     * 接收者id
     */
    private List<String> receiverId;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 验证码
     */
    private String checkCode;
}
