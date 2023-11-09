package com.ruoyi.daily.domain.asset.record.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 资产转移 DTO
 * Created by WuYang on 2022/8/22 17:47
 */
@Data
public class AssetDTO {
    /**
     * 唯一id
     */
    @NotNull
    private Long id ;

    /** 固定资产名称;最多18个字符,前端验证) */
    private String name ;
    /**
     * 原保管人
     */
    private String oldKeeper;

    /** 移至保管人 */
    private String toKeeper ;

    /** 移至部门 */
    @NotNull
    private Long departmentId ;




}
