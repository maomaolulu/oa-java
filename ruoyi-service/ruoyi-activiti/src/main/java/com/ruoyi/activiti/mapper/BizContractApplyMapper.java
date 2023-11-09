package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.fiance.BizContractApply;
import com.ruoyi.activiti.domain.dto.BizContractApplyDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 合同审批
 * 
 * @author zh
 * @date 2021-12-24
 */
@Repository
public interface BizContractApplyMapper extends BaseMapper<BizContractApply>
{
    @Select(" SELECT bu.proc_def_key,ca.create_by, bu.id buId,bu.proc_inst_id,bu.title,bu.STATUS,bu.result, " +
            " d.dept_name,d2.dept_name companyName,u.user_name createByName ,ca.id,ca.create_time,ca.update_time,ca.title,ca.cc,ca.apply_code, " +
            " ca.contract_code,ca.contract_name,ca.customer_name,ca.contract_date, " +
            " ca.project_name,ca.project_code,ca.expiry_date,ca.contract_amount,ca.remaining_money,ca.customer_contact, " +
            " ca.contact_information, u2.user_name followerName ,u.user_name createByName,d.dept_name,d3.dept_name followerDeptName,d.dept_id  " +
            " FROM biz_business bu " +
            " LEFT JOIN biz_contract_apply ca ON bu.table_id = ca.id " +
            " LEFT JOIN sys_dept d ON d.dept_id = ca.dept_id " +
            " LEFT JOIN sys_dept d2 ON d2.dept_id = ca.company_id " +
            " LEFT JOIN sys_dept d3 ON d3.dept_id = ca.follower_dept " +
            " LEFT JOIN sys_user u ON u.user_id = ca.create_by " +
            " LEFT JOIN sys_user u2 ON u2.user_id = ca.follower " +
            " ${ew.customSqlSegment} ")
    List<BizContractApplyDto> selectAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}