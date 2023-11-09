package com.alt.config.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 信道工具
 * @Author zx
 * @Date 2022/7/23 01:39
 * @Description RabbitMQUtil
 */

public class RabbitMqUtil {
    private static Channel channel = null;
    public static Channel getChannel() throws IOException, TimeoutException {
        if(channel != null){
            return channel;
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.114.40.118");
        factory.setPort(12335);
        factory.setUsername("root");
        factory.setPassword("123456");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        return channel;
    }
}
