package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CapitalGoodsDto {
    /** 资产ID */
    private String id;
    /** 采购编号 */
    private String purchaseCode;
    /** 申请部门 */
    private String deptName;
    private Long deptId;
    /** 申请人 */
    private String applicant;
    /** 物品类型 */
    private String goosType;
    /** 物品名称 */
    private String name;
    /** 规格型号 */
    private String model;
    /** 采购数量 */
    private Integer amount;
    /** 供应商 */
    private String dealer;
    /** 采购人 */
    private String purchaser;
    /** 采购时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseTime;
    /** 物品编号 */
    private String assetSn;
    /** 入库人 */
    private String operator;
    /** 单位 */
    private String unit;
    /** 入库时间 */
    private Date createTime;
    /** 品类编号 */
    private String spuSn;
    /** 申请公司 */
    private String companyName;
    private String companyId;
}
