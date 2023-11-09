package com.ruoyi.daily.domain.asset.category.dto;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * Author 郝佳星
 * Date 2022/6/8 9:33
 **/
@Data
public class SaveCategoryDTO {
    //公司id
    public Long companyId;
    //品类名称
    public String categoryName;
    //单位
    public String unit;
    public Date createTime;
    public String createBy;
}
