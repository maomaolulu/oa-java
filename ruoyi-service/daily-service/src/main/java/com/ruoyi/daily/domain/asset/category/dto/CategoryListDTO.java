package com.ruoyi.daily.domain.asset.category.dto;

import lombok.Data;

import java.math.BigInteger;

/**
 * Author 郝佳星
 * Date 2022/6/7 11:44
 **/
@Data
public class CategoryListDTO {
    //公司
    public Long companyId;

    //品类名称
    public String categoryName;
}
