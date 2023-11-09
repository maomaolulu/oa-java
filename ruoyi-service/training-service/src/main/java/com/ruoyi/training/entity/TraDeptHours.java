package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author hjy
 * @description 部门目标学时
 * @date 2022/6/15 14:47
 */
@Data
public class TraDeptHours extends BaseEntity {

    /**
     * 主键，自增
     */
    private Long id;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 老员工学时
     */
    private BigDecimal staffOldHours;
    /**
     * 新员工学时
     */
    private BigDecimal staffNewHours;
    /**
     * 关联信息-部门名称
     */
    private String deptName;
    /**
     * 关联数据-公司id
     */
    private Long companyId;


}
