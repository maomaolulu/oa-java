package com.ruoyi.file.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    /**
     * 服务地址
     */
    private String endpoint;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * excel 存储桶名称
     */
    private String bucketExcel;

    /**
     * image 存储桶名称
     */
    private String bucketImage;

    /**
     * pdf 存储桶名称
     */
    private String bucketPDF;

    /**
     * word 存储桶名称
     */
    private String bucketWord;

    /**
     * 创建连接
     *
     * @return {@link MinioClient}
     */
    @Bean
    public MinioClient getMinioClient()
    {
        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    }
}
