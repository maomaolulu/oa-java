package com.ruoyi.activiti.service;

/**
 * @Author yrb
 * @Date 2023/6/14 14:45
 * @Version 1.0
 * @Description 运营系统公共service
 */
public interface YunYingCommonService {
    /**
     * 获取token
     * @param processKey 流程key
     * @return token
     */
    String getToken(String processKey);
}
