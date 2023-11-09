package com.ruoyi.ehs.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

/**
 * Created by WuYang on 2022/8/18 14:06
 */
@Configurable
public class Swagger2Config {
    // 配置了Swagger的Docket的bean实例
    @Bean
    public Docket createDocket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                // 配置API文档的分组
                .groupName("Api-Groupg")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ruoyi.ehs.controller"))
                .build();
    }

    // 配置Swagger信息=apiInfo
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("名字", "https://www.baidu.com/", "邮箱");
        return new ApiInfo(
                "这是我的SwaggerAPI文档",
                "接口文档",
                "1.0",
                "https://www.baidu.com/",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList()
        );
    }

}
