package com.alt.config.mq;

import com.alt.entity.MessageRefEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 回调管理
 *
 * @author zx
 * @date 2022-09-07 19:14:26
 */
@Component
@Slf4j
public class CallbackConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    private final MongoTemplate mongoTemplate;
    @Autowired
    public CallbackConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 交换机不管是否收到消息的一个回调方法
     * CorrelationData
     * 消息相关数据
     * ack
     * 交换机是否收到消息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到 id 为:{}的消息", id);
        } else {
            mongoTemplate.updateFirst(new Query(where("_id").is(correlationData.getId())),
                    new Update().set("state", 1), MessageRefEntity.class);
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}", id, cause);
        }
    }


    /**
     * 当消息无法路由的时候的回调方法
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msgId = message.getMessageProperties().getHeaders().get("msgId").toString();
        log.error(" 消息： {}， 被交换机： {} 退回 ， 退回原因 :{}， 路由 key:{}，消息id：{}",
                new String(message.getBody()), exchange, replyText, routingKey,msgId);
        mongoTemplate.updateFirst(new Query(where("_id").is(msgId)),
                new Update().set("state", 3), MessageRefEntity.class);
    }
}