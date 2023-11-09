package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Table;


/**
 * @author : zh
 * @date : 2021-12-15
 * @desc : 收款账号
 */
@Data
@Table(name="biz_accounts_receivable")
public class BizAccountsReceivable extends BaseEntity{
    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 绑定用户 */
    private String belongUser ;
    /** 账户类型1支付宝2个人银行卡3对公银行账号 */
    private Integer accountType ;
    /** 户名 */
    private String name ;
    /** 账号（卡号） */
    private String accountNum ;
    /** 银行 */
    private String bank ;
    /** 支行地址（省） */
    private String province ;
    /** 支行地址（省） */
    @TableField(exist=false)
    private String provinceName ;
    /** 支行地址（市） */
    private String city ;
    /** 支行地址（市） */
    @TableField(exist=false)
    private String cityName ;
    /** 银行支行 */
    private String branchBank ;
    /** 逻辑删 */
    private String delFlag ;
    /** 查询类型 */
    @TableField(exist=false)
    private Integer types ;





}