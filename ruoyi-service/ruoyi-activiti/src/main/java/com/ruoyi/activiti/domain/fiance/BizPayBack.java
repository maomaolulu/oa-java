package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : zh
 * @date : 2021-12-26
 * @desc : 回款管理
 */
@Data
@Table(name="biz_pay_back")
public class BizPayBack implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 回款编号 */
    private String payBackCode ;
    /** 收款类型;1合同回款、2合同预付款） */
    private String receiveType ;
    /** 客户名称 */
    private String customerName ;
    /** 收款人 */
    private String payee ;
    /** 收款账户 */
    private String account ;

    /** 回款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date payBackDate ;
    /** 回款金额;元) */
    private BigDecimal amount ;

    /** 逻辑删 */
    private String delFlag ;
    /** 提交人 */
    private Long userId ;

    /** 创建人 */
    private Long createBy ;
    @TableField(exist = false)
    /** 创建人名称 */
    private String createByName;
    /** 部门id */
    private Long deptId ;
    /** 部门名称 */
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    /** 文件 */
    private List<SysAttachment> file;
    @TableField(exist = false)
    /** 图片 */
    private List<SysAttachment> img;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /** 合同审批id */
    private Long contractApplyId ;
    /** 合同金额 */
    @TableField(exist = false)
    private BigDecimal contractAmount;
    /** 合同日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date contractDate;
    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date expiryDate;
    /** 合同编号 */
    @TableField(exist = false)
    private String contractCode;
    /** 合同名称 */
    @TableField(exist = false)
    private String contractName;
    /** 剩余回款金额 */
    @TableField(exist = false)
    private BigDecimal remainingMoney;



}