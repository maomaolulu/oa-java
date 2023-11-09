package com.ruoyi.file;

import cn.hutool.cron.CronUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文件服务
 * @author zx
 * @date 2021/12/15 10:25
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
public class FileApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(FileApplication.class, args);
        CronUtil.setMatchSecond(true);
        CronUtil.start();
        System.out.println("file启动成功");
    }
}
