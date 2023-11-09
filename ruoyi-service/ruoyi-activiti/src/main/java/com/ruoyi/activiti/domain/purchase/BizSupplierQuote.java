package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商报价实体
 * @author zx
 * @date 2022/4/20 10:05
 */
@Data
@TableName("biz_supplier_quote")
public class BizSupplierQuote implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 采购物品id */
    private Long goodsId ;
    /** 供应商名称 */
    private String supplierName ;
    /** 报价（单价） */
    private BigDecimal quote ;
    /** 备注 */
    private String remark ;
}
