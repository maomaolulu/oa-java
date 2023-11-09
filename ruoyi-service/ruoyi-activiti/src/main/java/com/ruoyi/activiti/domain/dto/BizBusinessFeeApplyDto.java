package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BizBusinessFeeApplyDto {
    private Integer id;
    /**
     * 流程id
     */
    private Long buId;
    /**
     * 标题
     */
    private String title;
    /**
     * 抄送人
     */
    private String cc;
    /**
     * 抄送人
     */
    private String ccName;
    /**
     * 用户id
     */
    private Integer userId;  /**
     * 创建人
     */
    private String userName;
    /**
     * 部门id(数据权限)
     */
    private Integer deptId;
    /**
     * 部门
     */
    private String deptName;
    /**
     * 所属公司id
     */
    private Integer companyId;
    /**
     * 所属公司
     */
    private String companyName;
    /**
     * 申请编号
     */
    private String applyCode;
    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目总金额
     */
    private BigDecimal projectPrice;
    /**
     * 支付对象
     */
    private String paymentObject;
    /**
     * 报价单
     */
    private String quotation;
    /**
     * 申请人
     */
    private String applyUser;
    /**
     * 申请人
     */
    private String applyUserName;
    /**
     * 新老业务(0新业务，1续签业务)
     */
    private Integer ifOld;
    /**
     * 逻辑删
     */
    private String delFlag;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
    /**
     * 状态 1处理中 2结束
     */
    private Integer status;
    /** 流程id */
    private Long procInstId;
    /**
     * 结果状态  1处理中 2通过 3驳回
     */
    private Integer result;
    /**申请时间查询*/
    private String createTime1;
    /**申请时间查询*/
    private String createTime2;
    /**文件*/
    private  List<SysAttachment> sysAttachments;
    /** 流程 用于导出 */
    private List<Map<String,Object>> hiTaskVos;
    /** 申请时间 pdf导出 */
    private String pdfCreateTime;
    /** 到处人 pdf导出 */
    private String pdfName;
    /** 隶属部门id */
    private Long applyDeptId;
    /** 隶属部门名称 */
    private String applyDeptName;
    /** 隶属公司名称 */
    private String applyCompanyName;
    /** 是否是详情查询 1:是 */
    private String oneStatus;


}
