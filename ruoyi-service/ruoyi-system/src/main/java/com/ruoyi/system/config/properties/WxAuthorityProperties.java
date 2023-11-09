package com.ruoyi.system.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 小程序界面权限配置
 * 
 * @author ruoyi
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.wx")
@Data
public class WxAuthorityProperties
{
    /**
     * 小程序界面权限
     */
    private Set<String> wxAuthority = new HashSet<>();
}
