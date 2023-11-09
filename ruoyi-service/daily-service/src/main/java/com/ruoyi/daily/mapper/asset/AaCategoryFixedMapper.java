package com.ruoyi.daily.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.AaCategoryFixed;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/2 17:00
 * @Version 1.0
 * @Description 固定资产品类
 */
@Repository
public interface AaCategoryFixedMapper extends BaseMapper<AaCategoryFixed> {
    @Select("select t1.id,t1.category_name,t2.asset_name,t2.asset_type " +
            "from aa_category_fixed t1 " +
            "left join aa_asset_type t2 on t1.asset_type_id = t2.id " +
            "${ew.customSqlSegment} ")
    List<AaCategoryFixed> selectList(@Param(Constants.WRAPPER) Wrapper wrapper);
}
