package com.ruoyi.daily.domain.asset.transfer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 资产转移  dto
 * @author wuYang
 * @date 2022/9/13 8:56
 */
@Data
public class PurchaseTransferDTO {


    @ApiModelProperty("资产id")
    private Long id;


    /** 移至保管人 */
    private String toKeeper ;

    /** 移至保管人 */
    private Long keeperId ;

    /** 移至部门 */
    @NotNull
    private Long departmentId ;

    private Long goodsId;

    /**
     * 申请单号
     */
    private String purchaseCode;
    /**
     * 申请人
     */
    private String applyer;
}
