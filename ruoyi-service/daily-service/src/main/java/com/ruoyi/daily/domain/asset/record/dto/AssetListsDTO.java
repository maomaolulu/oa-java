package com.ruoyi.daily.domain.asset.record.dto;

import lombok.Data;

import java.util.List;

/**
 * 封装数组 前端传数组
 * Created by WuYang on 2022/8/24 10:58
 */
@Data
public class AssetListsDTO {
    /**
     * 前端请求列表
     */
    private List<AssetDTO> list;
}
