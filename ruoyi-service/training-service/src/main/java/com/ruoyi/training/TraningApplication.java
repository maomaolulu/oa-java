package com.ruoyi.training;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 培训考试
 * @author zx
 * @date 2022-05-30
 */
@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class TraningApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(TraningApplication.class, args);
        System.out.println("培训系统启动成功");
    }

}
