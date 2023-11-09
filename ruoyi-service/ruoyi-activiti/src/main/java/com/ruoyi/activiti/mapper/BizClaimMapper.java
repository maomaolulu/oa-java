package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.asset.BizClaim;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 领用申请
 * 
 * @author zh
 * @date 2022-1-6
 */
@Repository
public interface BizClaimMapper extends BaseMapper<BizClaim>
{
    @Select(" SELECT cl.*,sp.spu_sn,sp.name,sp.spu_type,sp.is_inspected,d.dept_name,sp.price,sp.model,sp.unit,sp.storage_num FROM biz_claim cl  " +
            "left join aa_spu sp on cl.spu_id=sp.id " +
            "left join sys_dept d on d.dept_id = cl.dept_id " +
            " ${ew.customSqlSegment } ")
    List<BizClaim> selectAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
    //出库
    @Select(" select sum(amount) from aa_sku where spu_id=#{supId } and operation=2 GROUP BY spu_id ")
    Long countExWarehouse (Long supId);
    //入库
    @Select(" select sum(amount) from aa_sku where spu_id=#{supId } and operation=1 GROUP BY spu_id ")
    Long countWarehousing (Long supId);
}