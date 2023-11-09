package com.ruoyi.ehs.domain.signin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/19 17:34
 */
@Data
public class BizSignInAddDTO {
    private List<String> timestamp;

    private BigDecimal lng;

    private BigDecimal lat;

    private List<String> img;
}
