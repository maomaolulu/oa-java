package com.ruoyi.daily.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.AaTranscation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 出入库记录
 * @author zx
 * @date 2022-08-08 19:22:49
 */
@Repository
public interface AaTranscationMapper extends BaseMapper<AaTranscation>  {
    /**
     * 查询出入库记录
     * @param wrapper
     * @return
     */
    @Select("select a.id, a.company_id, a.dept_id, a.name, a.model, a.item_type, a.item_sn, a.identifier, a.trans_type, a.spu_amount, a.amount, a.unit, a.is_return, a.applier, a.operator, a.create_time, a.create_by, a.update_by, a.update_time, " +
            "       d.dept_name,sd2.dept_name as company_name, " +
            "       case when a.trans_type = 1 then '直接入库' " +
            "           when a.trans_type = 2 then '采购入库' " +
            "           when a.trans_type = 3 then '直接出库' " +
            "           when a.trans_type = 4 then '领用出库' " +
            "           when a.trans_type = 5 then '报废出库' " +
            "           when a.trans_type = 6 then '退货出库' " +
            "           when a.trans_type = 7 then '盘盈入库' " +
            "           when a.trans_type = 8 then '盘亏出库' " +
            "           else '' end as trans_type_name " +
            "from aa_transcation a " +
            "left join sys_dept d on a.dept_id = d.dept_id " +
            "left join sys_dept sd2 on a.company_id = sd2.dept_id  ${ew.customSqlSegment} ")
    List<AaTranscation> selectTranscation(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
