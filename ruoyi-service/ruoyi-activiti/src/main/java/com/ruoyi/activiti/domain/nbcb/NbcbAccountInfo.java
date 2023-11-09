package com.ruoyi.activiti.domain.nbcb;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/6/6 9:54
 * @Version 1.0
 * @Description 账号信息
 */
@Data
@TableName("nbcb_account_info")
public class NbcbAccountInfo implements Serializable {
    private final static long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款账号
     */
    private String rcvAcc;

    /**
     * 收款方行名
     */
    private String rcvBankName;

    /**
     * 收款方联行号
     */
    private String rcvBankNo;

    /**
     * 收款户名
     */
    private String rcvName;

    /**
     * 付款金额
     */
    private String amt;

    /**
     * 申请编号(付款编号)
     */
    private String applyCode;

    /**
     * 付款单号
     */
    private String payBillNo;

    /**
     * 付款单提交状态
     */
    private Integer status;

    /**
     * 付款时间
     */
    private Date createTime;

    /**
     * 付款申请单提交失败的返回值
     */
    private String response;

    /**
     * 业务key
     */
    private String businessKey;

    /**
     * 结果状态
     */
    private Integer result;
}
