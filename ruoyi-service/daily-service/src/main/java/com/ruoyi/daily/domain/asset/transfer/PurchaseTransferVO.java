package com.ruoyi.daily.domain.asset.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ----------返回用户待确认采购移交列表 -----------
 * @author wuYang
 * @date 2022/9/13 13:33
 */
@Data
public class PurchaseTransferVO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("申请单号")
    private String purchaseCode;

    @ApiModelProperty("申请人")
    private String createBy;
    @ApiModelProperty("资产编号")
    private String assetSn;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("类型")
    private String model;
    @ApiModelProperty("保管者")
    private String keeper;
    @ApiModelProperty("部门")
    private String deptName;
    @ApiModelProperty("时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime purchaseDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    private String handler;



}
