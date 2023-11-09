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
import java.util.List;

/**
 * @author : zx
 * @date : 2022-3-17 20:51:54
 * @desc : 其他费用申请
 */
@Data
@Table(name="biz_other_charge_apply")
public class BizOtherChargeApply implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 其他费用申请单号 */
    private String otherCode ;
    /** 抄送人 */
    private String cc ;
    /** 标题 */
    private String title ;
    /** 部门id */
    private  Long deptId ;
    /** 公司id */
    private Long companyId ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人（申请人） */
    private String createBy ;
    /** 创建时间（申请时间） */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 付款事由 */
    private String paymentDetails ;
    /** 付款方式（1对公（有发票）、2对私（无发票）、3其他） */
    private String paymentMode ;
    /** 支付日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate ;
    /** 支付对象 */
    private String paymentObject ;
    /** 开户行 */
    private String bankOfDeposit ;
    /** 银行账户 */
    private String bankAccount ;
    /** 总计金额 */
    private BigDecimal amountTotal ;
    /** 创建人 */
    private Long userId ;
    /** 其他费用信息 */
    @TableField(exist=false)
    private List<BizOtherChargeInfo> otherChargeInfos;


}