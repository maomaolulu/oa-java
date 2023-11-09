package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BizPurchaseListDto {
    //----申请表 biz_purchase_apply----------------------------
    /**
     * 采购表id
     */
    private Integer id;

    /**
     * 采购申请编号
     */
    @Excel(name = "审批单号")
    private String purchaseCode;

    /**
     * 公司名称
     */
    @Excel(name = "申请公司")
    private String companyName;

    /**
     * 部门名字
     */
    @Excel(name = "申请部门")
    private String deptName;

    /**
     * 申请人姓名
     */
    @Excel(name = "申请人")
    private String createByName;

    /**
     * 资产类别
     */
    @Excel(name = "资产类别")
    private String itemTypeCn;

    /**
     * 物品名称
     */
    @Excel(name = "物品名称")
    private String name;

    /**
     * 单位
     */
    @Excel(name = "单位")
    private String unit;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

    /**
     * 数量
     */
    @Excel(name = "申购数量")
    private Integer amount;

    /**
     * 报价均价
     */
    @Excel(name = "供应商报价均价")
    private BigDecimal averageQuote;

    /**
     * 申请时间
     */
    @Excel(name = "申请时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 期望交付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望交付时间", dateFormat = "yyyy-MM-dd")
    private Date expectDate;

    /**
     * 备注
     */
    @Excel(name = "申购备注")
    private String remark;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    private String supplierName;

    /**
     * 备注（供应商）
     */
    @Excel(name = "备注（供应商）")
    private String remarkSupplier;

    /** 导出标识 */
    private String export;

    /**
     * 采购申请部门
     */
    private Integer deptId;

    /**
     * 申请人
     */
    private String createBy;

    /**
     * 申请时间查询
     */
    private String createTime1;
    /**
     * 申请时间查询
     */
    private String createTime2;
    /**
     * 申请标题
     */
    private String title;

    //----物品 biz_goods_info----------------------------
    /**
     * 物品id
     */
    private Integer goodsId;
    /**
     * 物品类型
     */
    private Integer itemType;

    /**
     * 实际数量
     */
    private Integer actualAmount;
    /**
     * 待入库数量
     */
    private Integer warehousingAmount;
    /**
     * 预估价
     */
    private BigDecimal price;
    /**
     * 采购价
     */
    private BigDecimal purchasePrice;
    /**
     * 采购单价
     */
    private String singlePrice;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 采购人
     */
    private String purchaser;
    /**
     * 采购人名字
     */
    private String purchaserName;
    /**
     * 采购时间
     */
    private String purchaseTime;
    /**
     * 是否验收
     */
    private String isAcceptance;
    /**
     * 是否采购
     */
    private String isPurchase;
    /**
     * 是否到货
     */
    private String isReceived;
    /**
     * 是否入库
     */
    private String isStorage;

    /**
     * 支付方式
     */
    private String paymentMethod;
    /**
     * 用途名称
     */
    private String usages;
    /**
     * 用途部门名称
     */
    private String usagesName;

    //-----------------------------------

    /**
     * 部门父级id
     */
    private String ancestors;
    //0:采购清单 1:采购计划  2:入库3:验收
    //用于搜索状态（0：等待采购；1：已采购；2:等待验收；3：已验收）
    private Integer status;

    /**
     * 审批状态 (0:已通过;1：审批中)
     */
    private Integer buStatus;

    private Integer result;
    /**
     * 审核状态
     */
    private String statusAndResult;
    /**
     * 订购采货状态
     */
    private String goodResult;

    /**
     * 到货日期
     */
    private Date arrivalDate;
    //----------------------------------------------------------------------
    /**
     * 库存id
     */
    private Long spuId;
    /**
     * 固定资产：0，流动资产：1
     */
    private String goodType;
    /**
     * 流程实例编号
     */
    private String procInstId;

    private AaSpu aaSpu;
    /**
     * 1：老数据 0：新数据
     */
    private Integer old;


    /**
     * 物品名称
     */
//    @Excel(name = "采购物品名称")
    private String applyName;
    /**
     * 物品规格型号
     */
//    @Excel(name = "采购物品规格型号")
    private String applyModel;
    /**
     * 单位
     */
//    @Excel(name = "采购单位")
    private String applyUnit;
    /**
     * 采购单位
     */
    private String applyRemark;
    /**
     * 是否移交
     */
    private String isTransfer;
    /**
     * 入库人
     */
    private String operator;
    /**
     * 入库时间
     */
    private Date storageTime;
    /**
     * 是否作废
     */
    private String isInvalid;

    /**
     * 固定资产id
     */
    private Long assetId;
    /**
     * 资产编号
     */
    private String assetSn;

    /**
     * 申请公司
     */
    private String companyId;
    /**
     * 付款人
     */
    private String payer;
    /**
     * 付款时间
     */
    private Date payTime;
}
