package com.ruoyi.daily.service.asset;

import com.ruoyi.daily.domain.asset.AaCategoryFixed;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/2 17:01
 * @Version 1.0
 * @Description 固定资产品类
 */
public interface AaCategoryFixedService {
    /**
     * 保存固定资产品类
     *
     * @param aaCategoryFixed 品类信息
     * @return result
     */
    boolean save(AaCategoryFixed aaCategoryFixed);

    /**
     * 获取固定资产列表
     *
     * @param aaCategoryFixed 固定资产品类信息
     * @return 集合
     */
    List<AaCategoryFixed> getInfo(AaCategoryFixed aaCategoryFixed);

    /**
     * 校验数据库是否存在要添加的品类名称
     *
     * @param aaCategoryFixed 品类名称
     * @return result
     */
    boolean checkUnique(AaCategoryFixed aaCategoryFixed);
}
