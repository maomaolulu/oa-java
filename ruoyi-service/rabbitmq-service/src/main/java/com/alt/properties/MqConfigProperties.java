package com.alt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置
 * @author zx
 * @date 2022-09-08 09:22:13
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "mq.config")
@Data
public class MqConfigProperties
{
    private String checkCode;
    private String expire;
}