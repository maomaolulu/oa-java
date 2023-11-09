package com.ruoyi.activiti.listener;

import com.nbcb.sdk.OpenSDK;
import com.ruoyi.activiti.properties.NbcbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author yrb
 * @Date 2023/6/7 13:54
 * @Version 1.0
 * @Description 初始化宁波银行SDK
 */
@Component
public class NbcbOpenSDKIniter {
    protected final Logger logger = LoggerFactory.getLogger(NbcbOpenSDKIniter.class);

    private final NbcbProperties nbcbProperties;

    public NbcbOpenSDKIniter(NbcbProperties nbcbProperties) {
        this.nbcbProperties = nbcbProperties;
    }

    @PostConstruct
    public void init() {
        try {
            String privateKey = nbcbProperties.getPrivateKey();
            Resource resource = new ClassPathResource("/conf/config.properties");
            boolean init = OpenSDK.init(resource.getInputStream(), privateKey);
            if (!init) logger.info("宁波银行OpenSDK初始化失败");
        } catch (Exception e) {
            logger.error("初始化宁波银行OpenSDK发生异常==================" + e);
        }
    }
}
