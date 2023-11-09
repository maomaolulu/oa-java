package com.ruoyi.activiti.domain.dto;

import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import lombok.Data;

import java.util.List;

/**
 * 采购专员编辑物品金额专用
 * @author zx
 * @date 2021/12/9 20:23
 */
@Data
public class GoodsRunVariableDto {
    private List<BizGoodsInfo> bizGoodsInfo;
    private String procInstId;
}
