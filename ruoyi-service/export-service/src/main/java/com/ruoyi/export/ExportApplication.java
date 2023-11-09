package com.ruoyi.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zx
 * @date 2022/8/2 10:41
 * @description ExportApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients(basePackages = "com.ruoyi")
public class ExportApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(ExportApplication.class, args);
    }
}
