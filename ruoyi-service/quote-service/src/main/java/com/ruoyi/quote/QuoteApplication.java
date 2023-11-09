package com.ruoyi.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author yrb
 * @Date 2022/4/24 15:27
 * @Version 1.0
 */
@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class QuoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuoteApplication.class, args);
        System.out.println("报价系统启动成功");
    }
}
