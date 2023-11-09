package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.purchase.BizAnticipateGoods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 采购预提交
 * 
 * @author zh
 * @date 2021-02-10
 */
@Repository
public interface BizAnticipateGoodsMapper extends BaseMapper<BizAnticipateGoods>
{
    @Select(" select * from biz_anticipate_goods ${ew.customSqlSegment} ")
    List<BizAnticipateGoods> listAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}