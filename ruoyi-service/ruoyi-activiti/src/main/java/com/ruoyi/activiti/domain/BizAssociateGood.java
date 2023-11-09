package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购物品关联付款报销
 * @author zx
 * @date 2022-06-28 11:43:48
 */
@TableName("biz_associate_good")
public class BizAssociateGood implements Serializable {
    private final static long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 主审批单类型 1付款2报销
     */
    private int types;
    /**
     * 主审批单id
     */
    private Long applyId;
    /**
     * 关联采购申请id
     */
    private Long associateId;
    /**
     * 物品id
     */
    private Long goodId;
    private Date createTime;
    private String createBy;

    private Long purchaseKey;

    public Long getPurchaseKey() {
        return purchaseKey;
    }

    public void setPurchaseKey(Long purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}
