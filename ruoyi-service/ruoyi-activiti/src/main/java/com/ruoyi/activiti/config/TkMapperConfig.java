package com.ruoyi.activiti.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan("com.ruoyi.*.mapper")
public class TkMapperConfig {
}