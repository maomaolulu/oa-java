package com.ruoyi.daily.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DailyVisitDto {
    /**打卡id*/
    private Long id;
    /** 部门id */
    private Long deptId ;
    /** 打卡日期 */
    private Date clockInDate ;
    /** 业务员所属公司 */
    private String company;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private Integer delFlag ;
    /** 创建人（打卡人） */
    private Long userId ;
    /** 创建人名称（打卡人名称） */
    private String createByName ;
    /** 工厂 */
    private String factory;
    /** 地址 */
    private String address;
}
