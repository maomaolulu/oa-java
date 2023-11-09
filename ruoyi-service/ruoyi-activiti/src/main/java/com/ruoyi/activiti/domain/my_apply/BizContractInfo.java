package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/5/22 9:39
 * @Version 1.0
 * @Description 合同评审-合同信息
 */
@Data
@TableName("biz_contract_info")
public class BizContractInfo implements Serializable {
    private final static long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id ;
    /**
     * 合同ID
     */
    private Long contractId;
    /**
     * 合同编号
     */
    private String identifier;
    /**
     * 合同类型
     */
    private String type;
    /**
     * 合同隶属公司
     */
    private String companyOrder;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 杭州隶属(业务来源)
     */
    private String businessSource;
    /**
     * 业务员
     */
    private String salesmen;
    /**
     * 委托类型
     */
    private String entrustType;
    /**
     * 委托单位名称(委托方)
     */
    private String entrustCompany;
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
     * 受检详细地址(所住地)
     */
    private String officeAddress;
    /**
     * 法定代表人
     */
    private String legalName;
    /**
     * 电话/固话
     */
    private String telephone;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话
     */
    private String mobile;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 标题
     */
    private String title;
    /**
     * 申请时间
     */
    private Date createTime;
    /**
     * 申请编号
     */
    private String applyCode;
    /**
     * 抄送人
     */
    private String cc;
    /**
     * 抄送人(中文)
     */
    private String ccCn;
}
