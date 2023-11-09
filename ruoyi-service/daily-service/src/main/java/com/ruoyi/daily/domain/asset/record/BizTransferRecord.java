package com.ruoyi.daily.domain.asset.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 移交记录
 * @author zx
 * @date 2022/4/22 14:35
 */
@TableName("biz_transfer_record")
@Data
public class BizTransferRecord implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间（移交时间） */
    private LocalDateTime createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 申请单号 */
    private String purchaseCode ;
    /** 申请人 */
    private String applyer ;
    /** 公司id */
    private Long companyId ;
    /** 归属部门 */
    private Long deptId ;
    /** 采购物品id */
    private Long goodsId ;
    /** 移交人 */
    private String handler ;
    /** 保管人 */
    private String keeper ;
    /** 移交开始时间 */
    @TableField(exist = false)
    private Date startTime ;
    /** 移交结束时间 */
     @TableField(exist = false)
    private Date endTime ;
    /**
     * 公司名称
     */
     @TableField(exist = false)
    private String companyName ;
    /**
     * 部门名称
     */
     @TableField(exist = false)
    private String deptName ;
    /**
     * 物品名称
     */
     @TableField(exist = false)
    private String name ;
    /**
     * 物品类型
     */
     @TableField(exist = false)
    private String dictLabel ;
    /**
     * 规格型号
     */
     @TableField(exist = false)
    private String model ;
    /**
     * 采购人
     */
     @TableField(exist = false)
    private String purchaser ;
    /**
     * 采购时间
     */
     @TableField(exist = false)
    private Date purchaseTime ;
    /**
     * 资产id
      */
    private Long assetId ;
    /**
     * 资产编号
     */
    @TableField(exist = false)
    private String assetSn ;

    /**
     * 是否确认移交
     */
    private Integer isChecked;
    /**
     * 保管人
     */
    private Long keeperId;
    /**
     * 原物品状态 0 库存 1 在用
     */
    private Integer originalState;

    /**
     * 当前物品状态 0 库存 1 在用
     */
    private Integer currentState;

    /**
     * 记录类型 0 采购移交 1 资产移交
     */
    private Integer type;
    /**
     * 原保管人
     */
    private String oldKeeper;
    /**
     * 原部门
     */
    private String oldDept;

    private Long oldDeptId;

}
