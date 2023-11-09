package com.ruoyi.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.activiti.domain.asset.Asset;
import com.ruoyi.activiti.mapper.AssetMapper;
import com.ruoyi.activiti.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 固定资产
 * @author: zx
 * @date: 2021/11/21 22:18
 */
@Service
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;

    @Autowired
    public AssetServiceImpl(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }


    /**
     * 根据id查询固定资产
     *
     * @param id
     * @return
     */
    @Override
    public Asset getById(Long id) {
        return assetMapper.selectById(id);
    }

    /**
     * 更新
     *
     * @param asset
     * @return
     */
    @Override
    public int update(Asset asset) {
        UpdateWrapper<Asset> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", asset.getId());
        return assetMapper.update(asset, updateWrapper);
    }
}
