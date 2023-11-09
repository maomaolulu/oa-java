package com.alt.config.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.alt.config.mq.MqConst.*;

/**
 * @author zx
 * @date 2022/8/30 17:39
 * @description TTL队列配置文件
 */
@Configuration
public class BasicConfig {

    @Bean("normalExchange")
    public DirectExchange normalExchange(){
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    @Bean("deadExchange")
    public DirectExchange deadExchange(){
        return new DirectExchange(DEAD_EXCHANGE);
    }

    @Bean("deadQueue")
    public Queue deadQueue(){
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    @Bean
    public Binding deadBinding(@Qualifier("deadQueue") Queue queued,@Qualifier("deadExchange")DirectExchange deadExchange){
        return BindingBuilder.bind(queued).to(deadExchange).with(MqConst.RK_DEAD);
    }

    @Bean("msg_callback")
    public Queue msgCallback(){
        return QueueBuilder.durable(MSG_CALLBACK).build();
    }
    @Bean
    public Binding callbackBinding(@Qualifier("msg_callback") Queue queued,@Qualifier("normalExchange")DirectExchange callbackExchange){
        return BindingBuilder.bind(queued).to(callbackExchange).with(MqConst.RK_CALLBACK);
    }
}
