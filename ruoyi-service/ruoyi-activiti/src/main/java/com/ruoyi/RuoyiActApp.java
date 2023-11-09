package com.ruoyi;

import com.ruoyi.system.annotation.EnableRyFeignClients;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
//@MapperScan("com.ruoyi.*.mapper")
@EnableRyFeignClients
@EnableScheduling
public class RuoyiActApp {
    public static void main(String[] args) {
        SpringApplication.run(RuoyiActApp.class, args);
        System.out.println("act启动成功");
    }
}
