package com.ruoyi.daily.domain.asset.record.dto;

import lombok.Data;

import java.util.List;

/**
 * 封装数组 前端传数组
 * Created by WuYang on 2022/8/24 11:04
 */
@Data
public class AssetStockListDTO {
    private List<AssetStockDTO> list;
}
