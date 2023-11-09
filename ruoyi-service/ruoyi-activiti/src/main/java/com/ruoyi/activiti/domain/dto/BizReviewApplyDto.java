package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.fiance.BizReviewInfo;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BizReviewApplyDto {
    private Long id ;
    /** 流程撤销id */
    private Long procInstId;
    /**
     * 流程id
     */
    private Long buId;
    /** 标题 */
    private String title ;
    /** 审批/服务费申请单号 */
    private String reviewCode ;
    /** 抄送人 */
    private String cc ;
    /** 抄送人 */
    private String ccName ;
    /** 部门id */
    private  Long deptId ;
    /** 公司id */
    private Long companyId ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人（申请人） */
    private String createBy ;
    /** 创建时间（申请时间） */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 款项用途（1评审费、2服务费、3其他费用） */
    private String types ;
    /** 付款事由 */
    private String paymentDetails ;
    /** 付款方式（1对公（有发票）、2对私（无发票）、3其他） */
    private String paymentMode ;
    /** 支付日期 (pdf评审日期) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate ;
    /** 支付对象 */
    private String paymentObject ;
    /** 开户行 */
    private String bankOfDeposit ;
    /** 银行账户 */
    private String bankAccount ;
    /** 附件 */
    private String file ;
    /** 总计金额 */
    private BigDecimal amountTotal ;
    /** 创建人 */
    private Long userId ;

    /**
     * 创建人
     */
    private String userName;
    /**
     * 申请人
     */
    private String applyUserName;
    /**
     * 部门
     */
    private String deptName;
    /**
     * 所属公司
     */
    private String companyName;
    /**
     * 状态 1处理中 2结束
     */
    private Integer status;
    /**
     * 结果状态  1处理中 2通过 3驳回
     */
    private Integer result;
    /**申请时间查询*/
    private String createTime1;
    /**申请时间查询*/
    private String createTime2;
    /** 评审服务信息 */
    private List<BizReviewInfo> bizReviewInfos;
    /**文件*/
    private  List<SysAttachment> sysAttachments;

    /** 流程 用于导出 */
    private List<Map<String,Object>> hiTaskVos;
    /** 申请时间 pdf导出 */
    private String pdfCreateTime;
    /** 到处人 pdf导出 */
    private String pdfName;
    /** pdf支付日期 */
    private String pdfPaymentDate ;
    /** pdf支付日期 */
    private String pdfAmountTotal ;
    /** 是否是详情查询 1：是  */
    private String isDetails;

    /**详情去除权限*/
    private Integer oneStatus;

}
