package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/8/8 13:43
 * @Version 1.0
 * @Description 仪器设备采购信息
 */

@Data
@TableName("biz_equip_warehouse")
public class BizEquipWarehouseRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 申请单号
     */
    private String purchaseCode;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 物品名称（设备名称）
     */
    private String goodsName;
    /**
     * 物品规格
     */
    private String model;
    /**
     * 物品单位
     */
    private String unit;
    /**
     * 采购单价
     */
    private BigDecimal singlePrice;
    /**
     * 采购时间
     */
    private Date purchaseDate;
    /**
     * 设备编号
     */
    private String equipCode;
    /**
     * 设备状态
     */
    private Integer status;
    /**
     * 保管人id
     */
    private Long keeperId;
    /**
     * 打印标签：0未打印、1已打印
     */
    private Integer printLabel;
    /**
     * 上传图片：0未上传，1已上传
     */
    private Integer uploadPic;
    /**
     * 录入人
     */
    private String createName;
    /**
     * 录入时间
     */
    private String createTime;
    /**
     * 入库人id
     */
    private Long userId;
    /**
     * 责任人id
     */
    private Long chargeId;
    /**
     * 打印条码
     */
    private String labelCode;
    /**
     * 当前估值
     */
    private String value;
    /**
     * 到货时间
     */
    private Date arrivedTime;
    /**
     * 出厂编号
     */
    private String factoryCode;
    /**
     * 维护人
     **/
    private String updateBy;
    /**
     * 维护时间
     **/
    private Date updateTime;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 生产厂家
     */
    private String productCompany;
    /**
     * 物品所在位置
     */
    private String location;
    /**
     * 责任人
     */
    @TableField(exist = false)
    private String chargeName;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;
    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;
    /**
     * 开始时间
     */
    @TableField(exist = false)
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField(exist = false)
    private Date endTime;
    /**
     * 资产类型
     */
    @TableField(exist = false)
    private String assetTypeName;
    /**
     * 品类名称
     */
    @TableField(exist = false)
    private String categoryName;
    /**
     * 保管人
     */
    @TableField(exist = false)
    private String keeper;
}
