package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.fiance.BizBusinessFeeApply;
import com.ruoyi.activiti.domain.dto.BizBusinessFeeApplyDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 业务费申请
 * 
 * @author zh
 * @date 2021-12-17
 */
@Repository
public interface BizBusinessFeeApplyMapper extends BaseMapper<BizBusinessFeeApply>
{
    @Select(" select bua.cc, bu.id buId, bu.proc_inst_id, bua.id ,bu.status,bu.result,bua.title,bua.apply_code,bua.project_code, " +
            "bua.project_name,bua.project_price,bua.payment_object,bua.quotation,bua.if_old,bua.create_time, " +
            "bua.update_time,bua.remark,d.dept_name,d2.dept_name companyName,u.user_name,u2.user_name applyUserName,bua.apply_dept_id ,bua.apply_user " +
            "from biz_business bu " +
            "left join biz_business_fee_apply bua on bu.table_id=bua.id " +
            "left join sys_dept d on d.dept_id = bua.dept_id  " +
            "left join sys_dept d2 on d2.dept_id = bua.company_id  " +
            "left join sys_user u on u.user_id=bua.user_id " +
            "left join sys_user u2 on u2.login_name = bua.apply_user " +
            " ${ew.customSqlSegment}  ")
    List<BizBusinessFeeApplyDto> selectAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);


}