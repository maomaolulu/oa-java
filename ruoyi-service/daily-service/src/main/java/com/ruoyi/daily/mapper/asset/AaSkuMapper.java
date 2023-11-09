package com.ruoyi.daily.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.AaSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * @date 2022-08-23 17:28:38
 */
@Repository
public interface AaSkuMapper extends BaseMapper<AaSku> {
    @Select("select k.id, k.company_id, k.dept_id, k.spu_id, k.state, k.operation, k.name, k.manufacturer, " +
            "k.factory_batch, k.stable_lmt, k.storage_loc, k.value, k.dealer, k.order_id, k.purchase_price, " +
            "k.purchase_time, k.arrive_time, k.notes, k.is_inspected, k.inspect_time, k.inspect_result, " +
            "k.inspect_cycle, k.permit, k.charger, k.keeper, k.operator, k.create_by, k.create_time, " +
            "k.update_by, k.update_time,k.amount,p.price as price,p.spu_type,p.model,sd.dept_name as company_name, " +
            "IF(k.is_inspected = 1, '是', '否') as is_inspected_name,p.unit,p.spu_sn,p.hazard_type," +
            "k.purchase_price as purchase_price_single,k.value as value_total,p.id as spu_id " +
            " from aa_sku k " +
            "left join aa_spu p on k.spu_id = p.id " +
            "left join sys_dept sd on sd.dept_id = p.company_id ${ew.customSqlSegment} ")
    List<AaSku> getSkuList(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    @Select("select k.id, k.company_id, k.dept_id, k.spu_id, k.state, k.operation, k.name, k.manufacturer, " +
            "k.factory_batch, k.stable_lmt, k.storage_loc, k.value, k.dealer, k.order_id, k.purchase_price, " +
            "k.purchase_time, k.arrive_time, k.notes, k.is_inspected, k.inspect_time, k.inspect_result, " +
            "k.inspect_cycle, k.permit, k.charger, k.keeper, k.operator, k.create_by, k.create_time, " +
            "k.update_by, k.update_time,k.amount as amount,p.price as price, p.spu_type,p.model,sd.dept_name as company_name, " +
            "IF(k.is_inspected = 1, '是', '否') as is_inspected_name,p.unit,p.spu_sn,p.hazard_type," +
            "k.purchase_price as purchase_price_single,k.value as value_total,p.id as spu_id " +
            " from aa_sku k " +
            "left join aa_spu p on k.spu_id = p.id " +
            "left join sys_dept sd on sd.dept_id = p.company_id ${ew.customSqlSegment} ")
    List<AaSku> getSkuListExcel(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
