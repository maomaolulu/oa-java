package com.ruoyi.activiti.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yrb
 * @Date 2023/6/14 13:32
 * @Version 1.0
 * @Description 运营系统配置参数
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "yunying")
@Data
public class YunYingProperties {
    /** 秘钥 */
    private String secretKey;
}
