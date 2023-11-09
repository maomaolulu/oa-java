package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/5/4 16:36
 * @Version 1.0
 * @Description 报价申请
 */
@Data
@TableName("biz_quotation_apply")
public class BizQuotationApply implements Serializable {
    private final static long serialVersionUID = 1L;
    @Id
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 部门id（数据权限）
     */
    private Long deptId;
    /**
     * 申请时间
     */
    private Date createTime;
    /**
     * 申请编号
     */
    private String applyCode;
    /**
     * 客户名称
     */
    private String company;
    /**
     * 报价单编号
     */
    private String code;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 检测地址
     */
    private String address;
    /**
     * 报价类型
     */
    private String quotationType;
    /**
     * 业务员（申请人）
     */
    private String salesmen;
    /**
     * 报价时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date quotationDate;
    /**
     * 审批原因
     */
    private String reviewReason;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 检测费
     */
    private BigDecimal detectFee;
    /**
     * 报告编制费
     */
    private BigDecimal reportFee;
    /**
     * 人工出车费
     */
    private BigDecimal trafficFee;
    /**
     * 总价
     */
    private BigDecimal totalMoney;
    /**
     * 业务费
     */
    private BigDecimal commission;
    /**
     * 分包费
     */
    private BigDecimal subprojectFee;
    /**
     * 评审费
     */
    private BigDecimal evaluationFee;
    /**
     * 优惠价
     */
    private BigDecimal preferentialFee;
    /**
     * 净值
     */
    private BigDecimal netvalue;
    /**
     * 申请人
     */
    private String applicant;
}
