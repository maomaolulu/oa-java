package com.ruoyi.activiti.domain.asset;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author zx
 * @date 2021/11/30 20:56
 */
@Data
@Table(name = "biz_claim_goods")
public class BizClaimGoods extends BaseEntity {
    @Id
    private Long  id ;
    /** 申领id */
    private Long claimId ;
    /** 物品id */
    private Long goodsId ;
    /** 申领数量 */
    private Long claimNum ;
    /** 逻辑删 */
    private String delFlag ;
    /**
     * 库存信息
     */
    @Transient
    private AaSpu aaSpu;

}
