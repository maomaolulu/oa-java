package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.dto.BizProjectAmountApplyDto;
import com.ruoyi.activiti.domain.my_apply.BizProjectAmountApply;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目金额调整申请
 *
 * @author zh
 * @date 2023/03/23
 */
@Repository
public interface BizProjectAmountApplyMapper extends BaseMapper<BizProjectAmountApply> {

    @Select("select count(*) from biz_project_amount_apply t1 " +
            "left join biz_business t2 on t1.id = t2.table_id " +
            "${ew.customSqlSegment}")
    int count(@Param(Constants.WRAPPER) QueryWrapper<BizProjectAmountApply> wrapper);

    @Select(" select bu.id buId, bu.proc_inst_id, bu.title,bu.status,bu.result,d.dept_name,bu.applyer userName, " +
            "ra.id,ra.create_time,ra.apply_code,ra.identifier,ra.up_result " +
            "from biz_business bu " +
            "left join biz_project_amount_apply ra on bu.table_id=ra.id " +
            "left join sys_dept d on d.dept_id = ra.dept_id " +
            " ${ew.customSqlSegment} ")
    List<BizProjectAmountApplyDto> selectAll(@Param(Constants.WRAPPER) QueryWrapper<BizProjectAmountApplyDto> wrapper);
}
