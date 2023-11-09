package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.Asset;

/**
 * @description: 固定资产
 * @author: zx
 * @date: 2021/11/21 22:17
 */
public interface AssetService {
    /**
     * 根据id查询固定资产
     * @param id
     * @return
     */
    Asset getById(Long id);

    /**
     * 更新
     * @param asset
     * @return
     */
    int update(Asset asset);
}
