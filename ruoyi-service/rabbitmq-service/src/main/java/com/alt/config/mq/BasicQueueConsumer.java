package com.alt.config.mq;

import com.alt.entity.MessageRefEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 死信队列消费者
 * @author zx
 * @date 2022/9/5 11:27
 * @description DeadLetterConsumer
 */
@Component
@Slf4j
public class BasicQueueConsumer {
    private final MongoTemplate mongoTemplate;
    @Autowired
    public BasicQueueConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @RabbitListener(queues = MqConst.DEAD_QUEUE)
    public void receiveDeadQueue(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        String msgId = message.getMessageProperties().getHeader("msgId").toString();
        mongoTemplate.updateFirst(new Query(where("_id").is(msgId)),
                new Update().set("state", 4), MessageRefEntity.class);
        log.info("当前时间：{},死信消息：{}",new Date(),msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(queues = MqConst.RK_CALLBACK)
    public void receiveCallbackQueue(Message message, Channel channel){
        String msgId = new String(message.getBody());
        mongoTemplate.updateFirst(new Query(where("_id").is(msgId)),
                new Update().set("state", 2), MessageRefEntity.class);
        log.info("当前时间：{},被消费的消息id：{}",new Date(),msgId);
    }

}
