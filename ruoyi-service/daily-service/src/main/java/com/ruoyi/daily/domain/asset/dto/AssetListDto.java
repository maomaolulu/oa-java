package com.ruoyi.daily.domain.asset.dto;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author zx
 * @date 2022/3/15 20:34
 */
@Data
public class AssetListDto {
    private String id;
    private String labelCode;
    private String asset_sn;
    private String name;
    private Integer asset_type;
    private Integer state;
    private String start_date;
    private String end_date;
    private BigInteger dept_id;
    private BigInteger company_id;
    private String charger;
    private String keeper;
    private Integer is_labelled;
    private Integer has_pic;
    private String keyword;
    private String category_id;
    private String category_name;
    private int excel;
}
