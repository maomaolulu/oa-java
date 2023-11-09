package com.ruoyi.gateway.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 放行白名单配置
 * 
 * @author ruoyi
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreWhiteProperties
{
    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> whites = new ArrayList<>();
//    /**
//     * 黑名单,禁止访问的地址
//     */
//    private List<String> blacks = new ArrayList<>();

//    public List<String> getBlacks() {
//        return blacks;
//    }
//
//    public void setBlacks(List<String> blacks) {
//        this.blacks = blacks;
//    }

    public List<String> getWhites()
    {
        return whites;
    }

    public void setWhites(List<String> whites)
    {
        this.whites = whites;
    }
}
