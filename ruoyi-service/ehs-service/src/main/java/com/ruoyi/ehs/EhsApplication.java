package com.ruoyi.ehs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wuYang
 * @date 2022/9/19 14:48
 */
@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class EhsApplication {
    public static void main(String[] args) {
        System.out.println("EHS服务启动成功！");
        SpringApplication.run(EhsApplication.class, args);
    }
}
