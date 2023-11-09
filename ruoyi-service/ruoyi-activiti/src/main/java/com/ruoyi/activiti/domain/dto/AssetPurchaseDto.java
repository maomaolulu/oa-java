package com.ruoyi.activiti.domain.dto;

import lombok.Data;

/**
 * 资产采购数据导出
 * @author zx
 * @date 2022/12/22 17:30
 * @description AssetPurchaseDto
 */
@Data
public class AssetPurchaseDto {
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;

}
