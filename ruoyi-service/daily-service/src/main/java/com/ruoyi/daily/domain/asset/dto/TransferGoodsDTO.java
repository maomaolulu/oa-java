package com.ruoyi.daily.domain.asset.dto;

import lombok.Data;

import java.util.List;

/**
 * 离职物品移交
 * @author zx
 * @date 2022-06-10 13:57:19
 */
@Data
public class TransferGoodsDTO {
    private String userName;
    private List<Long> assetId;
}
