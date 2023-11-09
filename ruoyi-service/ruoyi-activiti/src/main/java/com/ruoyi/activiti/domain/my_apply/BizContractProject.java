package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/6/13 13:48
 * @Version 1.0
 * @Description 合同项目信息修改
 */
@Data
@TableName("biz_contract_project")
public class BizContractProject implements Serializable {

    private final static long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目编号 */
    private String identifier;

    /** 项目类型 */
    private String type;

    /** 合同类型 */
    private String contractType;

    /** 合同编号 */
    private String contractIdentifier;

    /** 项目编号(新) */
    private String identifierNew;

    /** 项目类型(新) */
    private String typeNew;

    /** 合同类型(新) */
    private String contractTypeNew;

    /** 合同编号(新) */
    private String contractIdentifierNew;

    /** 抄送人 */
    private String cc ;

    /** 抄送人（中文） */
    @TableField(exist = false)
    private String ccName;

    /**
     * 项目状态(1项目录入，2任务分配，5采样，10收样，20检测报告，30报告装订，40质控签发，50报告寄送，60项目归档，70项目结束，98任务挂起，99项目中止)
     */
    private Integer status;

    /** 修改原因 */
    private String reason;

    /** 标题 */
    private String title;

    /** 创建时间 */
    private Date createTime;

    /** 创建人 */
    private String createBy ;

    /** 审批编号 */
    private String applyCode ;

    /** 审批人 */
    private String applier ;
}
