package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.car.BizCarApply;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 用车申请
 * 
 * @author zh
 * @date 2022-02-22
 */
@Repository
public interface BizCarApplyMapper extends BaseMapper<BizCarApply>
{
    @Select(" SELECT ca.*,bu.id buId ,bu.proc_inst_id ,bu.result ,d.dept_name deptName ,d2.dept_name companyName,u.user_name createByName, " +
            " ca.create_time " +
            " FROM `biz_car_apply`  ca  " +
            "left join biz_business bu on bu.table_id=ca.id " +
            "left join sys_dept d on d.dept_id=ca.dept_id " +
            "left join sys_dept d2 on d2.dept_id=ca.company_id " +
            "left join sys_user u on u.user_id=ca.create_by " +
            "${ew.customSqlSegment} "
      )
    List<BizCarApply> selectAll(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
    @Select("select * from ACT_RU_TASK where ID_ = #{id}")
    Task getTaskById(@Param("id") String id);


    //查询未还车分页数据
    @Select(" SELECT ca.*,bu.id buId ,bu.proc_inst_id ,d.dept_name deptName ,d2.dept_name companyName,u.user_name createByName, " +
            " ca.create_time " +
            " FROM `biz_car_apply`  ca  " +
            "left join biz_business bu on bu.table_id=ca.id " +
            "left join sys_dept d on d.dept_id=ca.dept_id " +
            "left join sys_dept d2 on d2.dept_id=ca.company_id " +
            "left join sys_user u on u.user_id=ca.create_by " +
            "left join biz_car_subsidy_apply su on ca.car_code=su.relation_code " +
            "${ew.customSqlSegment} "
    )
    List<BizCarApply> getUnpaidCarList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);




    //查询未还车分页数据
    @Select(" SELECT ca.*,bu.id buId ,bu.proc_inst_id ,d.dept_name deptName ,d2.dept_name companyName,u.user_name createByName, " +
            " ca.create_time, " +
            " de.time,de.delay_time as delayTime,de.id as delayId" +
            " FROM `biz_car_apply`  ca  " +
            "left join biz_business bu on bu.table_id=ca.id " +
            "left join sys_dept d on d.dept_id=ca.dept_id " +
            "left join sys_dept d2 on d2.dept_id=ca.company_id " +
            "left join sys_user u on u.user_id=ca.create_by " +
            "left join biz_car_subsidy_apply su on ca.car_code=su.relation_code " +
            "left join biz_car_delay de on de.aply_relation=ca.id " +
            "${ew.customSqlSegment} "
    )
    List<BizCarApply> getCarCheckList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}