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
 * 库存sku
 * @author zx
 * @date 2021/12/1 10:58
 */
@Table(name="aa_sku")
@Data
public class AaSku implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 公司id */
    private Long companyId ;
    /** 部门id */
    private Long deptId ;
    /** SPU;id */
    private Long spuId ;
    /** 1入库;2出库 */
    private Integer operation ;
    /** 耗材名称 */
    private String name ;
    /** 制造商 */
    private String manufacturer ;
    /** 经销商 */
    private String dealer ;
    /** 数量 */
    private Long amount ;
    /** 备注 */
    private String notes ;
    /** 采购时间 */
    private Date purchaseTime ;
    /** 责任人 */
    private String charger ;
    /** 金额;单价) */
    private BigDecimal value ;
    /** 保管人 */
    private String keeper ;
    /** 入库人 */
    private String operator ;
    /** 入库时间 */
    private Date time ;
    /** 订单ID */
    private String orderId;
    private Date createTime;
    /**
     * 品类信息
     */
    @TableField(exist=false)
    private AaSpu aaSpu;
}
