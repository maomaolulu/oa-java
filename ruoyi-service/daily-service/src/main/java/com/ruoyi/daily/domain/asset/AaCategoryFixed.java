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
 * @Date 2023/8/2 16:52
 * @Version 1.0
 * @Description 固定资产品类
 */
@Data
@TableName("aa_category_fixed")
public class AaCategoryFixed implements Serializable {
    private static final Long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品类名称（固定资产）
     */
    private String categoryName;

    /**
     * 资产类型id
     */
    private Long assetTypeId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 资产类型名称
     */
    @TableField(exist = false)
    private String assetName;

    /**
     * 资产类型：固定资产1，流动资产2
     */
    @TableField(exist = false)
    private Integer assetType;
}
