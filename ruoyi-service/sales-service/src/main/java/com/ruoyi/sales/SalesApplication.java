package com.ruoyi.sales;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 业务系统
 * @author zh
 * @date 2023-03-07
 */
@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class SalesApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(SalesApplication.class, args);
        System.out.println("业务系统启动成功");
    }

}
