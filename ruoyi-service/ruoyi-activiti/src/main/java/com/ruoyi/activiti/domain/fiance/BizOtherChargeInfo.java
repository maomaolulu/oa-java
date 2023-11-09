package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : zx
 * @date : 2022-3-17 20:48:18
 * @desc : 其他费用申请信息
 */
@Data
@Table(name="biz_other_charge_info")
public class BizOtherChargeInfo implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 申请id */
    private Long applyId;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime ;
    /** 支付金额 */
    @TableField(value = "payment_amount")
    private BigDecimal reviewAmount ;
    /** 费用明细 */
    private String amountDetails ;

    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfAmountDate ;
    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfReviewDate ;
    @TableField(exist = false)
    private BigDecimal paymentAmount ;

    public BigDecimal getPaymentAmount() {
        return this.reviewAmount==null?this.paymentAmount:this.reviewAmount;
    }
}
