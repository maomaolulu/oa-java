package com.ruoyi.activiti.domain.nbcb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author yrb
 * @Date 2023/5/15 16:47
 * @Version 1.0
 * @Description 明细信息
 */
@Data
public class EasyBatchTransferDtlReqBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "明细流水号")
    private String dtlSerialNo;

    @ApiModelProperty(value = "付款账号")
    private String payAcc;

    @ApiModelProperty(value = "收款账号")
    private String rcvAcc;

    @ApiModelProperty(value = "单位代码")
    private String corpCode;

    @ApiModelProperty(value = "收款方银行名")
    private String rcvBank;

    @ApiModelProperty(value = "收款方银行号")
    private String rcvBankCode;

    @ApiModelProperty(value = "收款方户名")
    private String rcvName;

    @ApiModelProperty(value = "明细项-金额 单位：元")
    private BigDecimal amt;

    @ApiModelProperty(value = "用途")
    private String purpose;
}
