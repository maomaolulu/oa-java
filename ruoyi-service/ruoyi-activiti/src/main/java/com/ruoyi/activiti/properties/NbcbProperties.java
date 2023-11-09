package com.ruoyi.activiti.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yrb
 * @Date 2023/6/7 20:58
 * @Version 1.0
 * @Description 宁波银行配置参数
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "nbcb")
@Data
public class NbcbProperties {
    /** 商户私钥 */
    private String privateKey;

    /** 客户号 */
    private String custId;
}
