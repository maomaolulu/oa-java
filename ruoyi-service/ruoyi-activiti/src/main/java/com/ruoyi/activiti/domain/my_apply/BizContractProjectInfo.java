package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author yrb
 * @Date 2023/5/22 9:51
 * @Version 1.0
 * @Description 合同评审-项目信息
 */
@Data
@TableName("biz_contract_project_info")
public class BizContractProjectInfo implements Serializable {
    private final static long serialVersionUID = 1L;
    @Id
    @KeySql(useGeneratedKeys = true)
    @TableId(type = IdType.AUTO)
    private Long id ;
    /**
     * 合同ID
     */
    private Long contractId;
    /**
     * 报价单编号
     */
    private String quotationCode;
    /**
     * 项目类型
     */
    private String type;
    /**
     * 受检单位
     */
    private String company;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区县
     */
    private String area;
    /**
     * 受检地址
     */
    private String officeAddress;
    /**
     * 检测项目
     */
    private String substances;
    /**
     * 应测点数
     */
    private Integer shouldPoint;
    /**
     * 实测点数
     */
    private Integer point;
    /**
     * 分包项目
     */
    private String isSubcontracts;
    /**
     * 优惠价
     */
    private BigDecimal preferentialFee;
    /**
     * 检测费
     */
    private BigDecimal detectFee;
    /**
     * 报告编制费
     */
    private BigDecimal reportFee;
    /**
     * 人工出车费
     */
    private BigDecimal trafficFee;
    /**
     * 业务费(元) 佣金金额
     */
    private BigDecimal commission;
    /**
     * 分包费(元)
     */
    private BigDecimal subprojectFee;
    /**
     * 评审费(元)
     */
    private BigDecimal evaluationFee;
    /**
     * 项目净值(元)
     */
    private BigDecimal netvalue;
}
