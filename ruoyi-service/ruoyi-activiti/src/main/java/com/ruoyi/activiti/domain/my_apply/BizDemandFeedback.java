package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 需求反馈
 * @author zx
 * @date 2022/3/21 10:44
 */
@Data
@TableName("biz_demand_feedback")
public class BizDemandFeedback implements Serializable {

    private final static long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 标题 */
    private String title ;
    /** 部门id（数据权限） */
    private Long deptId ;
    /** 抄送人 */
    private String cc ;
    /** 审批编号 */
    private String applyCode ;
    /** 实施内容 */
    private String content ;
    /** 审批详情 */
    private String detail ;
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
    /** 需求对接人 */
    private String demandContact ;
    /** 对接人联系方式 */
    private String contactTel ;
    /** 实施原因 */
    private String implementationReasons ;
    /** 实施后预计效果 */
    private String expectedEffect ;
    /**
     * 所属公司
     */
    private Long companyId;
    /**
     * 所属系统
     */
    private String affiliatedSystem;

    /**
     * 需求反馈凭证
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 需求反馈附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
    /** 抄送人名称 */
    @TableField(exist = false)
    private String ccName;
    /** 申请人公司名称 */
    @TableField(exist = false)
    private String deptName ;
    /** 创建人名称 */
    @TableField(exist = false)
    private String createByName ;
    /** 申请人部门名称 */
    @TableField(exist = false)
    private String companyName;
    /**
     * 所属系统名称
     */
    @TableField(exist = false)
    private String affiliatedSystemName;
}
