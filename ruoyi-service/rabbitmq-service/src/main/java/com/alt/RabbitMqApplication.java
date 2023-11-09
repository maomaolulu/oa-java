package com.alt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 消息队列服务
 * @author zx
 * @date 2022/8/30 16:51
 * @description RabbitMQApplication
 */
@SpringBootApplication(scanBasePackages = "com.alt")
@EnableDiscoveryClient
public class RabbitMqApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(RabbitMqApplication.class, args);
    }
}
