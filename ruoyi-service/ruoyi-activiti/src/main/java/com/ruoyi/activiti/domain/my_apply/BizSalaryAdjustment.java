package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 薪资调整
 * @author zx
 * @date 2022/3/8 21:15
 */
@Data
@TableName("biz_salary_adjustment")
public class BizSalaryAdjustment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 标题 */
    private String title ;
    /** 部门id（数据权限） */
    private Long deptId ;
    /** 抄送人 */
    private String cc ;
    /** 审批编号 */
    private String applyCode ;
    /** 部门 */
    private Long userDept ;
    /** 姓名 */
    private String name ;
    /**
     * 用户id
     */
    private Long userId ;
    /** 工号 */
    private String jobNumber ;
    /** 岗位名称 */
    private String post ;
    /** 调薪原因 */
    private String reason ;
    /** 原薪资（元） */
    private Double originalSalary ;
    /** 原薪资大写（元） */
    private String originalCapital ;
    /** 调整后薪资（元） */
    private Double adjustedSalary ;
    /** 调整后薪资大写（元） */
    private String adjustedCapital ;
    /** 调整月份 */
    private String adjustedMonth ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /**
     * 附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;
    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String userDeptName;

    /** 流程 用于导出 */
    @TableField(exist = false)
    private List<Map<String,Object>> hiTaskVos;
    /** 申请时间 pdf导出 */
    @TableField(exist = false)
    private String pdfCreateTime;
}
