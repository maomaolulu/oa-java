package com.ruoyi.activiti.domain.dto;

import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;
import com.ruoyi.system.domain.SysUser;
import lombok.Data;

/**
 * @Author yrb
 * @Date 2023/5/31 15:20
 * @Version 1.0
 * @Description
 */
@Data
public class BizQuotationApplyDTO {
    /**
     * 报价信息
     */
    private BizQuotationApply bizQuotationApply;
    /**
     * 用户信息
     */
    private SysUser user;
}
