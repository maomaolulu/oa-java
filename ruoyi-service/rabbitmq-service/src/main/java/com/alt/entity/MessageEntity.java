package com.alt.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息实体
 * @author zx
 * @date 2022-09-07 17:33:41
 */
@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;
    /**
     * 发送者id
     */
    @Indexed
    private String senderId;
    /**
     * 消息来源
     */
    private String source;
    /**
     * 发送时间
     */
    @Indexed
    private Date sendTime;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 来源ip
     */
    private String sourceIp;

}
