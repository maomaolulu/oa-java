package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeApply;
import com.ruoyi.activiti.domain.dto.BizCoverChargeApplyDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 服务费申请
 * 
 * @author zx
 * @date 2022年3月16日21:38:48
 */
@Repository
public interface BizCoverChargeApplyMapper extends BaseMapper<BizCoverChargeApply>
{
    @Select(" select bu.id buId, bu.proc_inst_id, bu.title,bu.status,bu.result,d.dept_name,d2.dept_name companyName,d2.dept_id companyId,u.user_name,ra.id, " +
            "ra.create_time,ra.update_time,ra.remark,ra.title,ra.cc,ra.dept_id,ra.cover_code, " +
            "ra.payment_details,ra.payment_mode,ra.payment_date, " +
            "ra.payment_object,ra.bank_of_deposit,ra.bank_account,ra.file,ra.amount_total " +
            "from biz_business bu " +
            "left join biz_cover_charge_apply ra on bu.table_id=ra.id " +
            "left join sys_dept d on d.dept_id = ra.dept_id   " +
            "left join sys_dept d2 on d2.dept_id = ra.company_id   " +
            "left join sys_user u on u.user_id=ra.user_id " +
            " ${ew.customSqlSegment} ")
    List<BizCoverChargeApplyDto> selectAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}