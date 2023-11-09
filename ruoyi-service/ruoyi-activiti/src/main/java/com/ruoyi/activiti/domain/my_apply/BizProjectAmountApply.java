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
import java.util.Map;

/**
 * 项目金额调整申请
 * @author zhanghao
 * @date 2023/03/23
 */
@Data
@TableName("biz_project_amount_apply")
public class BizProjectAmountApply implements Serializable {
    private final static long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 标题 */
    private String title ;
    /** 项目编号 */
    private String identifier ;
    /** 项目所属公司 */
    private String companyOrder ;
    /** 项目名称 */
    private String projectName ;
    /** 业务员 */
    private String salesmen ;
    /** 项目类型 */
    private String type ;
    /** 业务来源 */
    private String businessSource ;
    /** 部门id（数据权限） */
    private Long deptId ;
    /** 抄送人 */
    private String cc ;
    /** 抄送人 */
    @TableField(exist = false)
    private String ccName ;
    /** 审批编号 */
    private String applyCode ;


    //------------原始金额---------------------------
    /** 项目金额 */
    private Double totalMoney ;
    /** 业务费 */
    private Double commission ;
    /** 评审费 */
    private Double evaluationFee ;
    /** 服务费  */
    private Double serviceCharge ;
    /** 分包费  */
    private Double subprojectFee ;
    /** 虚拟税费 */
    private Double virtualTax ;
    /** 其他支出 */
    private Double otherExpenses ;


    //------------修改金额---------------------------
    /** 新项目金额 */
    private Double newTotalMoney ;
    /** 新业务费 */
    private Double newCommission ;
    /** 新评审费 */
    private Double newEvaluationFee ;
    /** 新服务费  */
    private Double newServiceCharge ;
    /** 新分包费  */
    private Double newSubprojectFee ;
    /** 新虚拟税费 */
    private Double newVirtualTax ;
    /** 新其他支出 */
    private Double newOtherExpenses ;


    /** 备注 */
    private String remark ;
    /** 修改结果 */
    private String upResult ;
    /** 修改结果 */
    private String upResultInfo ;
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
     * 附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
    /**
     * 图片
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;

    /** 流程 用于导出 */
    @TableField(exist = false)
    private List<Map<String,Object>> hiTaskVos;
    /** 申请时间 pdf导出 */
    @TableField(exist = false)
    private String pdfCreateTime;
}
