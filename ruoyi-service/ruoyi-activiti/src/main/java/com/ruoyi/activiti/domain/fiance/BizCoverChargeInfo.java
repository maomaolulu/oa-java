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
 * @date : 2022年3月16日21:30:35
 * @desc : 服务费申请信息
 */
@Data
@Table(name="biz_cover_charge_info")
public class BizCoverChargeInfo implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 服务id */
    private Long applyId;
    /** 备注（评审/服务/其他备注） */
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
    /** 项目编号（评审/服务费） */
    private String projectCode ;
    /** 受检单位（评审/服务） */
    private String inspectedUnit ;
    /**（支付金额） */
    @TableField(value = "payment_amount")
    private BigDecimal reviewAmount ;
    /** 合同金额 */
    private BigDecimal contractAmount ;

    /**
     * 回款金额
     */
    private BigDecimal payBackAmount;
    /** 收款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date amountDate ;

    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfAmountDate ;
    /** pdf收款日期 */
    @TableField(exist = false)
    private String pdfReviewDate ;

    public BigDecimal getPaymentAmount() {
        return this.reviewAmount == null ? this.paymentAmount : this.reviewAmount;
    }

    @TableField(exist = false)
    private BigDecimal paymentAmount ;

}
