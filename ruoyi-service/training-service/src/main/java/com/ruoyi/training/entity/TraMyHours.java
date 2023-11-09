package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author hjy
 * @description 我的学时，数据动态计算不存入数据库
 * @date 2022/6/15 14:45
 */
@Data
public class TraMyHours extends BaseEntity {

    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 新老员工标识  1 新员工   ；  2 老员工
     */
    private Integer userType;
    /**
     * 已加入课表学时
     */
    private BigDecimal chosenHours;
    /**
     * 已完成学时
     */
    private BigDecimal accomplishHours;
    /**
     * 目标学时（部门总学时）（区分新老员工）
     */
    private BigDecimal targetHours;
    /**
     * 当前年份
     */
    private Integer year;


}
