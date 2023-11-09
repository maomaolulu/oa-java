package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购物品信息对象 biz_goods_info
 * 
 * @author zx
 * @date 2021-11-16
 */
@Data
public class BizGoodsInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @Id
    private Long id;
    /**
     * 物品使用部门
     */
    @Transient
    private String usagesName;
    /** 费用归属部门名称 */
    @Transient
    private String ascriptionDeptName;
    /** 费用归属部门 */
    private Long ascriptionDept;
    /**
     * 部门id(数据权限)
     */
    private Integer deptId;

    /** 采购申请单id */
    @Excel(name = "采购申请单id")
    private Long purchaseId;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 采购日期 */
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    /** 物品名称 */
    @Excel(name = "物品名称")
    private String name;

    /** 物品类型:1办公用品,2采样耗材,3劳保用品,4实验室试剂耗材,5仪器设备,6仪器设备检定,7仪器设备维修及保养,8其他 */
    @Excel(name = "物品类型")
    private String itemType;
    @Transient
    private String itemTypeName;

    /** 物品规格型号 */
    @Excel(name = "物品规格型号")
    private String model;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 物品数量 */
    @Excel(name = "物品数量")
    private Integer amount;

    /** 实际数量(采购可修改) */
    @Excel(name = "实际数量(采购可修改)")
    private Integer actualAmount;
    /** 待入库数量 */
    private Integer warehousingAmount;
    /** 入库数量 */
    @Transient
    @TableField(exist = false)
    private Integer actualWarehousingAmount;

    /** 采购物品实际使用方向(用途) */
    @Excel(name = "采购物品实际使用方向(用途)")
    private String usages;

    /** 期望交付日期 */
    @Excel(name = "期望交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectDate;

    /**
     * 是否入库
     */
    @Excel(name = "是否入库")
    private String isStorage;

    /**
     * 是否完全交付
     */
    @Excel(name = "是否完全交付")
    private String isReceived;

    /**
     * 是否采购
     */
    @Excel(name = "是否采购")
    private String isPurchase;

    /** 逻辑删 */
    private String delFlag;

    /**
     * 预估价
     */
    private BigDecimal price;
    /** 采购价 */
    private BigDecimal purchasePrice ;
    /** 供应商 */
    private String supplier ;
    /** 采购人 */
    private String purchaser ;
    /** 采购时间 */
    private Date purchaseTime ;
    /** 是否验收 */
    private String isAcceptance ;
    /** 采购单价 */
    private BigDecimal singlePrice ;
    /** 支付方式 */
    private String paymentMethod;
    /** 到货日期 */
    private Date arrivalDate;
    /** 库存id */
    private Long spuId;
    /** 固定资产：0，流动资产：1 */
    private String goodType;
    @Transient
    private Long anticipateGoodsId;
    /** 物品名称 */
//    @Excel(name = "采购物品名称")
    private String applyName;
    /** 物品规格型号 */
//    @Excel(name = "采购物品规格型号")
    private String applyModel;
    /** 单位 */
//    @Excel(name = "采购单位")
    private String applyUnit;
    /**
     * 采购单位
     */
    private String applyRemark;

    /**
     * 供应商报价均价
     */
    private BigDecimal averageQuote;

    /**
     * 是否移交
     */
    private String isTransfer;
    /**
     * 是否作废
     */
    private String isInvalid;
    /**
     * 是否作废 0正常1作废2退货
     */
    @Transient
    private String isInvalidName;
    /**
     * 已移交数量
     */
    private Integer transferNum;
    /**
     * 财务状态 0未关联1关联付款2关联报销3已付款4已报销
     */
    private Integer financeStatus;
    /**
     * 财务状态 0未关联1关联付款2关联报销3已付款4已报销
     */
    @Transient
    private String financeStatusName;
    /**
     * 采购状态
     */
    @Transient
    private String purchaseStatus;
    /**
     * 付款人
     */
    private String payer;
    /**
     * 付款时间
     */
    private Date payTime;
    /**
     * 财务状态颜色
     */
    @Transient
    private String financeColor;
    /**
     * 申购状态颜色
     */
    @Transient
    private String purchaseColor;

    /**
     * 关联采购的主审批单id
     */
    @Transient
    private List<Long> applyIds;

}