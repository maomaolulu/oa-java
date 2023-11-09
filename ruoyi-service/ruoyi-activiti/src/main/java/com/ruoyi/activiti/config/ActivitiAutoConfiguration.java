package com.ruoyi.activiti.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiAutoConfiguration implements ProcessEngineConfigurationConfigurer {

    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        // 其他配置
        // 配置自定义用户任务解析器
        CustomBpmnJsonConverter.initCustomJsonConverter();
    }
}