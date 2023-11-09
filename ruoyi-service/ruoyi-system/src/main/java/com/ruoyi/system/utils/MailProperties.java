package com.ruoyi.system.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 默认抄送角色配置
 * @author zx
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailProperties
{
    private String host;
    private String username;
    private String password;
    private Integer port;
}