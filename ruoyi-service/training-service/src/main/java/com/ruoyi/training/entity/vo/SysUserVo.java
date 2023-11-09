package com.ruoyi.training.entity.vo;

import lombok.Data;

/**
 * 人员数据载体
 *
 * @author hjy
 * @date 2022/7/5 18:55
 */
@Data
public class SysUserVo {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 部门id
     */
    private String deptName;
    /**
     * 公司id
     */
    private Long  companyId;

}
