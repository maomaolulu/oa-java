package com.ruoyi.activiti.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 默认抄送角色配置
 * @author zx
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "cc")
@Data
public class CcProperties
{
    private Map<String,String> mapValue;

    public String get(String key) {
        return mapValue.get(key);
    }
}