package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteChargeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/11 21:24
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteChargeCategoryVO extends QuoteChargeCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<QuoteChargeCategory> childrenList = new ArrayList<>();
}
