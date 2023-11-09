package com.ruoyi.activiti.vo;

import lombok.Data;

/**
 * 检查库存
 * @author zx
 * @date 2021/12/26 16:02
 */
@Data
public class SkuCheckVo {
    private Long spuId;
    private Long amount;
    private String name;
}
