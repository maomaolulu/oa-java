package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 付款申请
 * @author zx
 * @date 2021/12/16 10:38
 */
@Data
@TableName("biz_payment_apply")
public class BizPaymentApply implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 申请人部门 */
    private Long deptId ;
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
    @TableField(exist = false)
    private Map<String,Object> pdfMap;



    /** 关联审批单 */
    private String paymentType ;
    @TableField(exist = false)
    private String paymentTypeName ;
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

    /**
     * 付款申请编号
     */
    private String paymentCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 抄送人
     */
    private String cc;
    /** 款项类目 */
    private String projectType ;
    /** 项目名称 */
    private String projectName ;
    /** 付款总金额 */
    private BigDecimal projectMoney ;
    /** 项目编号 */
    private String projectCode ;
    /** 已付金额 */
    private BigDecimal paidCount ;
    /** 本次付款总金额 */
    private BigDecimal payCount ;
    /** 账户类型 */
    private String accountType ;
    @TableField(exist = false)
    private String accountTypeName ;
    /** 支付对象(户名) */
    private String name ;
    /** 支付对象(账号) */
    private String accountNum ;
    /** 支付对象(开户行) */
    private String bank ;
    /** 支行地址（省） */
    private String province ;
    /** 支行地址（市） */
    private String city ;
    /** 银行支行 */
    private String branchBank ;
    /** 特殊备注 */
    private String remarkSpec ;
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
     * 付款明细数组
     */
    @TableField(exist = false)
    private List<BizPaymentDetail> paymentDetails;
    /**
     * 付款凭证
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 付款附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;

    /** 流程 用于导出 */
    @TableField(exist = false)
    private List<Map<String,Object>> hiTaskVos;

    /** 抄送人名称 */
    @TableField(exist = false)
    private String ccName;
    /**
     * 审批单导出字段
     */
    @TableField(exist = false)
    private String pdfAssociateApply;

    @TableField(exist = false)
    private String provinceName;
    @TableField(exist = false)
    private String cityName;
}
