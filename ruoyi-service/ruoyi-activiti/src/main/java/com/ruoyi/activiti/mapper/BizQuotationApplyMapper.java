package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author yrb
 * @Date 2023/5/4 18:14
 * @Version 1.0
 * @Description
 */
@Repository
public interface BizQuotationApplyMapper extends BaseMapper<BizQuotationApply> {
    @Select(" select t2.* from biz_quotation_apply t1 " +
            " left join biz_business t2 on t1.id = t2.table_id " +
            " ${ew.customSqlSegment} ")
    BizBusiness selectOneRecord(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
