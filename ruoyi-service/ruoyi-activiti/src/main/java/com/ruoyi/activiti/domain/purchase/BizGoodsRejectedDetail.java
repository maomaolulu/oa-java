package com.ruoyi.activiti.domain.purchase;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.activiti.domain.asset.AaSku;
import com.ruoyi.activiti.domain.asset.Asset;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

/**
 * lx
 * 22/01/14
 */
@Data
@Table(name="biz_goods_rejected_detail")
public class BizGoodsRejectedDetail implements Serializable {
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 关联退货ID */
    private Long goodsRejectedId;
    /** asset_id/sku_id */
    private Long assertId;
    /** 数量 */
    private Integer num;
    /** 备注 */
    private String remarks;
    /** 物品类型:1资产,2耗材 */
    private Integer itemType;
    @TableField(exist = false)
    private Asset asset;
    @TableField(exist = false)
    private AaSku aaSku;
    @TableField(exist = false)
    private Map<String,Object> infoMap;
    /**
     * 采购编号
     */
    private String purchaseCode;
}
