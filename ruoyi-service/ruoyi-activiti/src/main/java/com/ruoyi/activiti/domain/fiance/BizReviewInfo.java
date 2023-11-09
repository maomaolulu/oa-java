package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : zh
 * @date : 2021-12-20
 * @desc : 审批/服务费申请信息
 */
@Data
@Table(name="biz_review_info")
public class BizReviewInfo implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 评审/服务id */
    private Long applyId;
    /** 备注（评审/服务/其他备注） */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime ;
    /** 项目编号（评审/服务费） */
    private String projectCode ;
    /** 受检单位（评审/服务） */
    private String inspectedUnit ;
    /** 评审日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reviewDate ;
    @TableField(exist = false)
    private String reviewDateStr;
    /** 评审金额（支付金额） */
    private Double reviewAmount ;
    /** 评审老师 */
    private String reviewTeacher ;
    /** 合同金额 */
    private Double contractAmount ;
    /** 费用明细 */
    private String amountDetails ;
    /** 收款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date amountDate ;
    /** 专家人数 */
    private Integer expertNum ;
    /** 实际评审日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualReviewDate ;

    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfAmountDate ;
    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfReviewDate ;

    /**
     * 项目负责人
     */
    private String projectUser;



}
