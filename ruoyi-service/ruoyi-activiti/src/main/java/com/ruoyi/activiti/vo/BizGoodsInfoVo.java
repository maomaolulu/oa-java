package com.ruoyi.activiti.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 采购物品信息对象 biz_goods_info
 * 
 * @author zx
 * @date 2021-11-16
 */
public class BizGoodsInfoVo
{

    /** id */
    private Long id;

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
    private Integer itemType;

    /** 物品规格型号 */
    @Excel(name = "物品规格型号")
    private String model;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 物品数量 */
    @Excel(name = "物品数量")
    private Integer amount;

    /** 实际数量(验收时清点) */
    @Excel(name = "实际数量(验收时清点)")
    private Integer actualAmount;

    /** 采购物品实际使用方向(用途) */
    @Excel(name = "采购物品实际使用方向(用途)")
    private String usages;

    /** 期望交付日期 */
    @Excel(name = "期望交付日期", width = 30, dateFormat = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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

    public String getIsStorage() {
        return isStorage;
    }

    public void setIsStorage(String isStorage) {
        this.isStorage = isStorage;
    }

    public String getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(String isReceived) {
        this.isReceived = isReceived;
    }

    public String getIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(String isPurchase) {
        this.isPurchase = isPurchase;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPurchaseId(Long purchaseId) 
    {
        this.purchaseId = purchaseId;
    }

    public Long getPurchaseId() 
    {
        return purchaseId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }
    public void setUnit(String unit) 
    {
        this.unit = unit;
    }

    public String getUnit() 
    {
        return unit;
    }
    public void setAmount(Integer amount)
    {
        this.amount = amount;
    }

    public Integer getAmount()
    {
        return amount;
    }
    public void setActualAmount(Integer actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public Integer getActualAmount()
    {
        return actualAmount;
    }
    public void setUsages(String usages) 
    {
        this.usages = usages;
    }

    public String getUsages() 
    {
        return usages;
    }
    public void setExpectDate(Date expectDate) 
    {
        this.expectDate = expectDate;
    }

    public Date getExpectDate() 
    {
        return expectDate;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("purchaseId", getPurchaseId())
            .append("name", getName())
            .append("model", getModel())
            .append("unit", getUnit())
            .append("amount", getAmount())
            .append("actualAmount", getActualAmount())
            .append("usages", getUsages())
            .append("expectDate", getExpectDate())
            .append("delFlag", getDelFlag())
            .toString();
    }
}