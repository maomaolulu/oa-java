package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 耗材盘点
 * @author zx
 * @date 2022/3/29 14:05
 */
@Data
@TableName("biz_consumables_inventory")
public class BizConsumablesInventory implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
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
    /** 审批编号 */
    private String applyCode ;
    /** 标题 */
    private String title ;
    /** 抄送人 */
    private String cc ;
    /** 申请部门 */
    private Long deptId ;
    /** 申请公司 */
    private Long companyId ;
    /** 盘点类型1盈2亏 */
    private Integer types ;
    /** 盘点原因 */
    private String reason ;

    /**
     * 盘点明细
     */
    @TableField(exist = false)
    private List<BizConsumablesInventoryDetail> details;

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

}
