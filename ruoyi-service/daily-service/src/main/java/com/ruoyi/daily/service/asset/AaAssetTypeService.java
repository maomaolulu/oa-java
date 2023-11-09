package com.ruoyi.daily.service.asset;

import com.ruoyi.daily.domain.asset.AaAssetType;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/2 14:55
 * @Version 1.0
 * @Description 资产类型
 */
public interface AaAssetTypeService {
    /**
     * 查询资产类型列表
     *
     * @param aaAssetType 资产类型
     * @return 集合
     */
    List<AaAssetType> getList(AaAssetType aaAssetType);

    /**
     * 新增资产类型信息
     *
     * @param aaAssetType 资产类型信息
     * @return result
     */
    boolean save(AaAssetType aaAssetType);

    /**
     * 校验数据库是否存在要添加的资产类型
     *
     * @param aaAssetType 资产类型名称
     * @return result
     */
    boolean checkUnique(AaAssetType aaAssetType);
}
