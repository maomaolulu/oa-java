package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : zh
 * @date : 2021-12-24
 * @desc : 合同审批
 */
@Data
@Table(name="biz_contract_apply")
public class BizContractApply implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 抄送人 */
    private String cc;
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
    /** 所属公司 */
    private Long companyId ;
    /** 当前登录人部门id;数据权限） */
    private Long deptId ;
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
    /** 跟进人 */
    private Long follower ;
    /** 跟进人部门 */
    private Long followerDept ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;


}