package com.ruoyi.daily.service.impl.asset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.asset.AaAssetType;
import com.ruoyi.daily.mapper.asset.AaAssetTypeMapper;
import com.ruoyi.daily.service.asset.AaAssetTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/2 14:57
 * @Version 1.0
 * @Description 资产类型
 */
@Service
@Slf4j
public class AaAssetTypeServiceImpl implements AaAssetTypeService {
    private final AaAssetTypeMapper assetTypeMapper;

    public AaAssetTypeServiceImpl(AaAssetTypeMapper assetTypeMapper) {
        this.assetTypeMapper = assetTypeMapper;
    }

    /**
     * 查询资产类型列表
     *
     * @param aaAssetType 类型信息
     * @return 集合
     */
    @Override
    public List<AaAssetType> getList(AaAssetType aaAssetType) {
        QueryWrapper<AaAssetType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(aaAssetType.getAssetType() != null, "asset_type", aaAssetType.getAssetType());
        queryWrapper.ne(StrUtil.isNotBlank(aaAssetType.getExcludeEquip()),"id",53);
        return assetTypeMapper.selectList(queryWrapper);
    }

    /**
     * 新增资产类型信息
     *
     * @param aaAssetType 资产类型信息
     * @return result
     */
    @Override
    public boolean save(AaAssetType aaAssetType) {
        return assetTypeMapper.insert(aaAssetType) > 0;
    }

    /**
     * 校验数据库是否存在要添加的资产类型
     *
     * @param aaAssetType 资产类型名称
     * @return result
     */
    @Override
    public boolean checkUnique(AaAssetType aaAssetType) {
        QueryWrapper<AaAssetType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_name",aaAssetType.getAssetName());
        return CollUtil.isEmpty(assetTypeMapper.selectList(queryWrapper));
    }
}
