package com.ruoyi.quote.domain.dto;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author yrb
 * @Date 2022/5/10 11:04
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SheetFactorItemsDTO extends BaseEntity implements Serializable {
    /** 因素名称 */
    @Excel(name = "因素名称")
    private String factorName;

    /** 检测标准及编号 */
    @Excel(name = "检测标准及编号")
    private String standardInfo;

    /** 限制范围 */
    @Excel(name = "限制范围")
    private String limitRange;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 点位数 */
    @Excel(name = "点位数")
    private BigDecimal pointNumber;

    /** 小计 */
    @Excel(name = "小计")
    private BigDecimal totalPrice;
}
