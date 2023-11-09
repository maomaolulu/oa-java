package com.ruoyi.daily.domain.asset.record.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产移交查询分页记录 dto
 * Created by WuYang on 2022/8/23 14:02
 */
@Data
public class AssetSelectDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String categoryName;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 物品编号
     */
    private String assetSn;
    /**
     * 物品名称
     */
    private String name;
    /**
     * 原保管者
     */
    private String oldKeeper;
    /**
     * 当前保管者
     */
    private String keeper;
    /**
     * 当前时间
     */
    private String start;
    /**
     * 结束时间
     */
    private String end;
    /**
     *  不传 后端自动处理
     */
    private LocalDateTime startTime;
    /**
     *  不传 后端自动处理
     */
    private LocalDateTime endTime;
    /**
     * 是否确认
     */
    private Integer isCheck;


}
