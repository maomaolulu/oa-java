package com.alt.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 消息子表
 * @author zx
 * @date 2022-09-06 20:01:45
 *
 */
@Document(collection = "message_ref")
@Data
public class MessageRefEntity implements Serializable {
    @Id
    private String _id;
    /**
     * 消息id
     */
    private String messageId;
    /**
     * 接收人id
     */
    @Indexed
    private String receiverId;
    /**
     * 消息状态 0默认值 1发送到交换机失败 2接收成功 3回退消息
     */
    @Indexed
    private Integer state;

}

