package com.ruoyi.daily.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/8/2 14:47
 * @Version 1.0
 * @Description 资产类型
 */
@Data
@TableName("aa_asset_type")
public class AaAssetType implements Serializable {
    private static final Long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id ;

    /**
     * 资产类型名称
     */
    private String assetName;

    /**
     * 资产类型：固定资产1，流动资产2
     */
    private Integer assetType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 排除仪器设备
     */
    @TableField(exist = false)
    private String excludeEquip;
}
