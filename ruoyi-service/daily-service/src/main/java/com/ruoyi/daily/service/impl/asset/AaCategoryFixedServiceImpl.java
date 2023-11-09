package com.ruoyi.daily.service.impl.asset;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.asset.AaCategoryFixed;
import com.ruoyi.daily.mapper.asset.AaCategoryFixedMapper;
import com.ruoyi.daily.service.asset.AaCategoryFixedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/2 17:01
 * @Version 1.0
 * @Description 固定资产品类
 */
@Service
@Slf4j
public class AaCategoryFixedServiceImpl implements AaCategoryFixedService {
    private final AaCategoryFixedMapper aaCategoryFixedMapper;

    public AaCategoryFixedServiceImpl(AaCategoryFixedMapper aaCategoryFixedMapper) {
        this.aaCategoryFixedMapper = aaCategoryFixedMapper;
    }

    /**
     * 保存固定资产品类
     *
     * @param aaCategoryFixed 品类信息
     * @return result
     */
    @Override
    public boolean save(AaCategoryFixed aaCategoryFixed) {
        return aaCategoryFixedMapper.insert(aaCategoryFixed) > 0;
    }

    /**
     * 获取固定资产列表
     *
     * @param aaCategoryFixed 固定资产品类信息
     * @return 集合
     */
    @Override
    public List<AaCategoryFixed> getInfo(AaCategoryFixed aaCategoryFixed) {
        QueryWrapper<AaCategoryFixed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(aaCategoryFixed.getAssetTypeId() != null, "t1.asset_type_id", aaCategoryFixed.getAssetTypeId());
        return aaCategoryFixedMapper.selectList(queryWrapper);
    }

    /**
     * 校验数据库是否存在要添加的品类名称
     *
     * @param aaCategoryFixed 品类名称
     * @return result
     */
    @Override
    public boolean checkUnique(AaCategoryFixed aaCategoryFixed) {
        QueryWrapper<AaCategoryFixed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_type_id",aaCategoryFixed.getAssetTypeId());
        queryWrapper.eq("category_name",aaCategoryFixed.getCategoryName());
        return CollUtil.isEmpty(aaCategoryFixedMapper.selectList(queryWrapper));
    }
}
