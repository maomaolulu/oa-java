package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * lx
 * 22/01/14
 */
@Data
@Table(name="biz_goods_rejected_apply")
public class BizGoodsRejectedApply implements Serializable {
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 标题 */
    private String title;
    /** 部门id */
    private Long deptId ;
    /** 抄送人 */
    private String cc;
    @TableField(exist = false)
    private String ccName;
    /** 审批编号 */
    private String applyCode;
    /** 退货原因 */
    private String returnReason;
    /** 备注 */
    private String remark;
    /** 逻辑删除 */
    private Integer delFlag;
    /** 创建人 */
    private String createBy;
    /** 创建时间 */
    private Date createTime;
    /** 更新人*/
    private String updateBy;
    /** 更新时间 */
    private Date updateTime;
    /** 关联子表数组 */
    @TableField(exist = false)
    private List<BizGoodsRejectedDetail> goodsRejectedDetailList;
    /**
     * 退货凭证
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 退货附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;


}
