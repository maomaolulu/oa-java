package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 还车补贴申请
 * 
 * @author zh
 * @date 2022-02-24
 */
@Repository
public interface BizCarSubsidyApplyMapper extends BaseMapper<BizCarSubsidyApply>
{
    @Select(" SELECT ca.*,bu.id buId ,bu.proc_inst_id ,d.dept_name deptName " +
            " ,d2.dept_name companyName,u.user_name createByName,bu.result,bu.status FROM `biz_car_subsidy_apply`  ca  " +
            "left join biz_business bu on bu.table_id=ca.id " +
            "left join sys_dept d on d.dept_id=ca.dept_id " +
            "left join sys_dept d2 on d2.dept_id=ca.company_id " +
            "left join sys_user u on u.user_id=ca.create_by " +
            "${ew.customSqlSegment} "
      )
    List<BizCarSubsidyApply> selectAll(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}