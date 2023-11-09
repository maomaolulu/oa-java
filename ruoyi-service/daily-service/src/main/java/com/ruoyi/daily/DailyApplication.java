package com.ruoyi.daily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 日报服务
 *
 * @author zx
 * @date 2021/12/15 10:25
 */
@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class DailyApplication {
    public static void main(String[] args) {
        SpringApplication.run(DailyApplication.class, args);
        System.out.println("daily启动成功");
    }
}
