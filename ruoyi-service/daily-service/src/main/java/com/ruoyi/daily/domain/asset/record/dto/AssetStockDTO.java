package com.ruoyi.daily.domain.asset.record.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by WuYang on 2022/8/22 17:55
 */
@Data
public class AssetStockDTO {
    /** 库存编号;12位数字) */
    @NotNull
    private Long id ;

    /** 固定资产名称;最多18个字符,前端验证) */
    private String name ;


    /**
     * 原部门
     */
    private Long oldDept;


    /**
     * 保管人
     */
    @NotEmpty(message = "保管人不能为空")
    private String keeper;


}
