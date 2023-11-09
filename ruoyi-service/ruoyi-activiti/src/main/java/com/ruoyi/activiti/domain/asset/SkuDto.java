package com.ruoyi.activiti.domain.asset;

import lombok.Data;

/**
 * 直接出库参数
 * @author zx
 * @date 2022-08-31 15:09:53
 */
@Data
public class SkuDto {
    /**
     * 数量
     */
    private Integer amount;
    /**
     * 领用人
     */
    private String applier;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 经办人
     */
    private String operator;
    /**
     * 品类id
     */
    private Long spuId;
    /**
     * 出入库类型
     */
    private Integer transType;
}
