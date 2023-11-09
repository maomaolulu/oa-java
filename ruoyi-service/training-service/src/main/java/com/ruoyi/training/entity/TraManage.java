package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 培训管理-载体
 *
 * @author hjy
 * @date 2022/6/17 13:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TraManage extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 部门id集合
     */
    private List<Long> deptIds;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 已完成学时
     */
    private BigDecimal accomplishHours;
    /**
     * 目标学时（部门自定义总学时）
     */
    private BigDecimal targetHours;
    /**
     * 截止日期
     */
    private String deadline;
    /**
     * 公司id-新需求，默认按照登录人所属公司筛选
     */
    private Long companyId;

}
