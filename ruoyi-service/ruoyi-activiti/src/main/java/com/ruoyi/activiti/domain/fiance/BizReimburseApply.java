package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报销申请
 * @author zx
 * @date 2021/12/16 10:38
 */
@Data
@TableName("biz_reimburse_apply")
public class BizReimburseApply implements Serializable  {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 隶属公司/部门 */
    private Long  deptId ;
    /** 隶属公司/部门 */
    private Long subjectionDeptId;
    /** 申请人公司名称 */
    @TableField(exist = false)
    private String deptName ;
    /** 创建人名称 */
    @TableField(exist = false)
    private String createByName ;
    /** 申请人部门名称 */
    @TableField(exist = false)
    private String companyName;
    /**隶属部门名称 */
    @TableField(exist = false)
    private String subjectionDeptName;
    /**隶属公司名称 */
    @TableField(exist = false)
    private String subjectionCompanyName;
    /** 申请时间 pdf导出 */
    @TableField(exist = false)
    private String pdfCreateTime;
    /** 到处人 pdf导出 */
    @TableField(exist = false)
    private String pdfName;

    /** 流程 用于导出 */
    @TableField(exist = false)
    private List<Map<String,Object>> hiTaskVos;
    /** 关联审批单 */
    private String reimburseType ;
    @TableField(exist = false)
    private String reimburseTypeName ;
    /** 审批单号 */
    private String auditCode ;
    /**
     * 关联多个审批单
     */
    @TableField(exist = false)
    private List<Map<String,Object>> associateApply;
    /**
     * 关联多个采购单
     */
    @TableField(exist = false)
    private List<Map<String,Object>> associatePurchaseApply;
    /** 报销编号 */
    private String reimbursementCode ;
    /** 报销总金额 */
    private BigDecimal reimburseMoneyTotal ;
    /** 特殊备注 */
    private String remarkSpec ;
    /**
     * 抄送人
     */
    private String cc;
    /**
     * 备注
     */
    private String remark;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime ;
    /** 账户类型 */
    private String accountType ;
    @TableField(exist = false)
    private String accountTypeName ;
    /** 户名 */
    private String name ;
    /** 账号 */
    private String accountNum ;
    /** 银行 */
    private String bank ;
    /**
     * 报销明细数组
     */
    @TableField(exist = false)
    private List<BizReimburseDetail> reimburseDetails;
    /**
     * 报销凭证
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 报销附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;

    /**
     * 抄送人名称
     */
    @TableField(exist = false)
    private String ccName;

    /**
     * 标题
     */
    @TableField(exist = false)
    private String title;
}
