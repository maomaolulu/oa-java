package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款申请明细
 * @author zx
 * @date 2021/12/19 19:49
 */
@Data
@TableName("biz_payment_detail")
public class BizPaymentDetail implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 付款申请id */
    private Long parentId ;
    /** 本次支付金额 */
    private BigDecimal thisPayment ;
    /** 费用明细 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 电子发票 */
    private String invoice;
}
