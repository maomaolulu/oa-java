package com.ruoyi.daily.domain.asset.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 责任物品
 * @author zx
 * SELECT
 *   a.id,
 * 	CONCAT(c.dept_name,'-',d.dept_name) as belong_dept,
 * 	sd.dict_label as asset_type,
 * 	a.asset_sn,
 * 	a.name ,
 * 	a.model,
 * 	a.unit,
 * 	a.charger,
 * 	a.keeper,
 * 	a.purchase_price as single_price
 * FROM
 * 	aa_asset a
 * 	LEFT JOIN sys_dept c ON a.company_id = c.dept_id
 * 	LEFT JOIN sys_dept d ON a.dept_id = d.dept_id
 * 	LEFT JOIN sys_dict_data sd on sd.dict_code = a.asset_type
 * 	LEFT JOIN sys_dict_data sd2 on sd2.dict_value = a.state and sd2.dict_type = 'goodStatus'
 *
 */
@Data
public class DutyAssetDto {

    private Long id;
    /**
     * 所属部门
     */
    private String belongDept;
    /**
     * 资产类型
     */
    private String assetTypeName;
    private Integer assetType;
    /**
     * 资产编号
     */
    private String assetSn;
    /**
     * 物品状态
     */
    private String goodStatus;
    /**
     * 资产名称
     */
    private String name;
    /**
     * 规格型号
     */
    private String model;
    /**
     * 单位
     */
    private String unit;
    /**
     * 责任人
     */
    private String charger;
    /**
     * 保管人
     */
    private String keeper;
    /**
     * 单价
     */
    private BigDecimal singlePrice;

    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 采购时间
     */
    private Date purchaseTime;
}
