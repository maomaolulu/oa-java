package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : zh
 * @date : 2022-1-6
 * @desc : 领用申请
 */
@Data
@Table(name="biz_claim")
public class BizClaim implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 部门id */
    private Long deptId ;
    /** 库存id */
    private Long spuId ;
    /** 申请数量 */
    private Integer amount ;

    /** 批准人 */
    private String approver ;
    /** 批准人id */
    private Long approverId ;
    /** 申请人 */
    private String createByName ;
    /** 是否批准（0：为批准，1：已批准,2:已驳回） */
    private String status ;
    /** 备注 */
    private String remark ;
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
    //物品编码
    @TableField(exist = false)
    private String spuSn;
    //资产分类
    @TableField(exist = false)
    private String spuType;
    //资产分类名称
    @TableField(exist = false)
    private String spuTypeName;
    //是否已检定
    @TableField(exist = false)
    private String isInspected;
    //公司名称
    @TableField(exist = false)
    private String companyName;
    //部门名称
    @TableField(exist = false)
    private String deptName;
    //物品名称
    @TableField(exist = false)
    private String name;
    /** 剩余数量 */
    @TableField(exist = false)
    private Long skAmount ;
    /** 价格 */
    @TableField(exist = false)
    private BigDecimal price ;
    /** 规格型号 */
    @TableField(exist = false)
    private String model;
    /** 规格型号 */
    @TableField(exist = false)
    private String unit;

    /** 当前库存 */
    @TableField(exist = false)
    private Integer storageNum;
}
