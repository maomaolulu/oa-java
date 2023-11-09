package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BizContractApplyDto {
    private Long id ;
    /** 流程撤销id */
    private Long procInstId;
    /**
     * 流程id
     */
    private Long buId;
    /** 抄送人 */
    private String cc;
    /** 抄送人 */
    private String ccName;
    /**标题 */
    private String title;
    /** 审批编号 */
    private String applyCode ;
    /** 合同编号 */
    private String contractCode ;
    /** 合同名称 */
    private String contractName ;
    /** 客户名称 */
    private String customerName ;
    /** 合同日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractDate ;

    /** 创建人id */
    private Long createBy ;
    /** 创建人名称 */
    private String createByName ;
    /** 所属公司 */
    private Long companyId ;
    /** 所属公司名称 */
    private String companyName ;
    /** 当前登录人部门id;数据权限） */
    private Long deptId ;
    /** 当前登录人部门名称 */
    private String deptName ;

    /** 跟进人id */
    private Long follower ;
    /** 跟进人名称 */
    private String followerName ;
    /** 跟进人部门 */
    private Long followerDept ;
    /** 跟进人部门名称 */
    private String followerDeptName ;


    /** 项目名称 */
    private String projectName ;
    /** 项目编号 */
    private String projectCode ;
    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate ;
    /** 合同金额;元） */
    private BigDecimal contractAmount ;
    /** 剩余回款金额 */
    private BigDecimal remainingMoney ;
    /** 客户联系人 */
    private String customerContact ;
    /** 联系方式 */
    private String contactInformation ;

    /** 逻辑删 */
    private String delFlag ;

    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
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
    /**文件*/
    private  List<SysAttachment> sysAttachmentsFile;
    /**图片*/
    private  List<SysAttachment> sysAttachmentsImg;
    /** 流程Key */
    private String procDefKey;
    /** 1:回款查询 */
    private String isPayBack;
    /**详情去除权限*/
    private Integer oneStatus;
}
