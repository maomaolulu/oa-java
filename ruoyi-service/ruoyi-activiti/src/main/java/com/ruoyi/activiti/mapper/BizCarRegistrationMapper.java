package com.ruoyi.activiti.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.car.BizCarRegistration;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 汽车管理
 * 
 * @author zh
 * @date 2022-02-21
 */
@Repository
public interface BizCarRegistrationMapper extends BaseMapper<BizCarRegistration>
{
    @Select(" select ca.id,ca.plate_number,ca.frame_number,ca.company_id,ca.dept_id,ca.car_brand,ca.people_number,ca.types " +
            " ,ca.remark,ca.create_time,d.dept_name deptName,d2.dept_name companyName,ca.car_user_name,ca.latest_mileage, " +
            " ca.status_update_user,ca.status_update_time,ca.status_update_reason,ca.status,ca.car_user,ca.last_maintainance_num,ca.car_insurance_period " +
            " from biz_car_registration ca  " +
            " left join sys_dept d on d.dept_id=ca.dept_id " +
            " left join sys_dept d2 on d2.dept_id=ca.company_id" +
            "   ${ew.customSqlSegment} "
      )
    List<BizCarRegistration> selectAll(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 查询可预约车辆
     * @param queryWrapper
     * @return
     */
    @Select(" select ca.id,ca.plate_number,ca.frame_number,ca.company_id,ca.dept_id,ca.car_brand,ca.people_number,ca.types " +
            " ,ca.remark,ca.create_time,ca.car_user_name,ca.latest_mileage, " +
            " ca.status_update_user,ca.status_update_time,ca.status_update_reason,ca.status,ca.car_user " +
            " from biz_car_registration ca  " +
            " left join sys_dept d on d.dept_id=ca.dept_id " +
            "   ${ew.customSqlSegment} "
    )
    List<BizCarRegistration> selectPlateNumber(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}