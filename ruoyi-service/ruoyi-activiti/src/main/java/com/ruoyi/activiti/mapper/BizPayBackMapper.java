package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.fiance.BizPayBack;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 回款管理
 * 
 * @author zh
 * @date 2021-12-26
 */
@Repository
public interface BizPayBackMapper extends BaseMapper<BizPayBack>
{
    @Select(" SELECT d.dept_name,u.user_name create_byName,pb.id,pb.create_time,pb.update_time,pb.pay_back_code, " +
            " pb.receive_type,pb.customer_name,pb.payee,pb.account, " +
            " pb.contract_apply_id,pb.pay_back_date,pb.amount,ca.contract_amount,ca.contract_amount,ca.contract_date, " +
            " ca.expiry_date,ca.contract_code,ca.contract_name,ca.remaining_money " +
            "FROM biz_pay_back pb " +
            " LEFT JOIN biz_contract_apply ca on ca.id=pb.contract_apply_id " +
            " LEFT JOIN sys_dept d ON d.dept_id = pb.dept_id " +
            " LEFT JOIN sys_user u ON u.user_id = pb.create_by " +
            " ${ew.customSqlSegment} ")
    List<BizPayBack> selectAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}