package com.alt.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import com.alt.config.mq.CallbackConfig;
import com.alt.config.mq.MqConst;
import com.alt.config.mq.RabbitMqUtil;
import com.alt.entity.MessageEntity;
import com.alt.entity.MessageRefEntity;
import com.alt.entity.dto.MessageDto;
import com.alt.properties.MqConfigProperties;
import com.alt.service.MessageService;
import com.alt.utils.RedisService;
import com.rabbitmq.client.Channel;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.alt.config.mq.MqConst.DEAD_EXCHANGE;
import static com.alt.config.mq.MqConst.NORMAL_EXCHANGE;

/**
 * @author zx
 * @date 2022/9/6 19:51
 * @description MessageServiceImpl
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MqConfigProperties mqConfigProperties;
    private final RabbitTemplate rabbitTemplate;
    private final MongoTemplate mongoTemplate;
    private final CallbackConfig confirmCallback;
    private final RedisService redisService;

    @Autowired
    public MessageServiceImpl(MqConfigProperties mqConfigProperties, RabbitTemplate rabbitTemplate, MongoTemplate mongoTemplate, CallbackConfig confirmCallback, RedisService redisService) {
        this.mqConfigProperties = mqConfigProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.mongoTemplate = mongoTemplate;
        this.confirmCallback = confirmCallback;
        this.redisService = redisService;
    }
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(confirmCallback);
        /**
         * true： * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false： * 如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(confirmCallback);
    }
    /**
     * 保存消息
     *
     * @param message 消息实体
     */
    @Override
    public void save(MessageDto message) throws IOException, TimeoutException {
        String hostIp = IpUtils.getIpAddr(ServletUtils.getRequest());
        Object cacheObject = redisService.getCacheObject("black:"+hostIp );
        if(cacheObject!=null){
            throw new StatefulException(hostIp+"已被封禁，请联系管理员解锁");
        }
        if(!mqConfigProperties.getCheckCode().equals(message.getCheckCode())){
            redisService.setCacheObject("black:"+hostIp,new Date().toString());
            throw new StatefulException(hostIp+"验证码不正确,已封禁ip");
        }
        log.info(hostIp);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMsg(message.getMsg());
        messageEntity.setSenderId(message.getSenderId());
        messageEntity.setSource(message.getSource());
        messageEntity.setSendTime(new Date());
        messageEntity.setSourceIp(hostIp);
        MessageEntity insert = mongoTemplate.insert(messageEntity);
        log.error(insert.get_id());
        Channel channel = RabbitMqUtil.getChannel();
        for (String rid : message.getReceiverId()) {
            MessageRefEntity messageRefEntity = new MessageRefEntity();
            messageRefEntity.setMessageId(insert.get_id());
            messageRefEntity.setReceiverId(rid);
            messageRefEntity.setState(0);
            MessageRefEntity ref = mongoTemplate.insert(messageRefEntity);

            String routingKey = message.getSource() + rid;

            CorrelationData correlationData=new CorrelationData(ref.get_id());
            rabbitTemplate.convertAndSend(NORMAL_EXCHANGE, routingKey, message.getMsg(), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setHeader("msgId",ref.get_id());
                    messageProperties.setExpiration(mqConfigProperties.getExpire());
                    return message;
                }
            },correlationData);
        }
        log.info("当前时间：{} 消息：{}", new Date(), message.getMsg());
    }
}
