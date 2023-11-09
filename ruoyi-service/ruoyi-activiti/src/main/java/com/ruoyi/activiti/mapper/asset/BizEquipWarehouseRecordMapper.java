package com.ruoyi.activiti.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.asset.BizEquipWarehouseRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/8 14:00
 * @Version 1.0
 * @Description 设备入库信息
 */
@Repository
public interface BizEquipWarehouseRecordMapper extends BaseMapper<BizEquipWarehouseRecord> {

    @Select("select t1.company_id,t1.dept_id,t1.goods_name,t1.model,t1.unit,t1.single_price " +
            ",t1.purchase_date, t1.equip_code,t1.`status`,t1.keeper_id,t1.create_name,t1.create_time, " +
            "t1.print_label,t1.upload_pic,t1.user_id,t1.label_code from biz_equip_warehouse t1 " +
            "${ew.customSqlSegment} ")
    List<BizEquipWarehouseRecord> selectBizEquipWarehouseRecordList(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
