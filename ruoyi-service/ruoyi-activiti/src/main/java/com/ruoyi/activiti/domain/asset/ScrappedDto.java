package com.ruoyi.activiti.domain.asset;

import lombok.Data;

/**
 * 报废列表参数
 * @author zx
 * @date 2022-09-05 14:27:00
 */
@Data
public class ScrappedDto {
    /**
     * 申请编号
     */
    private String applyCode;
    /**
     * 物品名称
     */
    private String goodsName;
    /**
     * 物品编号
     */
    private String assetSn;
}
