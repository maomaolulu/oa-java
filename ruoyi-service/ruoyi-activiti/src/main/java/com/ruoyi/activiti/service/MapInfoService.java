package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.proc.BizBusiness;

import java.util.Map;

/**
 * 小程序主要信息
 * @author zx
 * @date 2022/2/26 11:52
 */
public interface MapInfoService {
    /**
     * 获取各个流程详情主要信息
     * @param bizBusiness
     * @return
     */
    Map<String,Object> getMapInfo(BizBusiness bizBusiness);

    /**
     * 主要信息html
     * @param bizBusiness
     * @return
     */
    String getMapInfoMail(BizBusiness bizBusiness);
}
