package com.ruoyi.activiti.domain.dto;

import lombok.Data;

@Data
public class GoodsRejectedApplyDto {
    /** 审批编号 */
    private String purchaseCode;
    /** 资产编号/耗材编号 */
    private String itemSn;
    /** 物品名称 */
    private String name;
    /** 物品类型 */
    private String model;
    /** 物品数量 */
    private Long amount;
    /** 单位 */
    private String unit;

}
