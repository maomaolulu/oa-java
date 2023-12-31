package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.ruoyi.system.annotation.EnableRyFeignClients;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableRyFeignClients
public class RuoYiAuthApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiAuthApp.class, args);
        System.out.println("auth启动成功");
    }
}