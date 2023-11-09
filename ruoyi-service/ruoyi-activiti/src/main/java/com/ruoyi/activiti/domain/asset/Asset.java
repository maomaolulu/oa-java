package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 固定资产
 * @author: zx
 * @date: 2021/11/21 22:01
 */
@Data
@TableName("aa_asset")
public class Asset implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存编号;12位，公司+部门+随机数）
     */
    private String labelCode;

    /**
     * 状态:1在用;2准用,3待修,4维修中,5送检中,6报废,7外借,8封存
     */
    private Integer state;

    /**
     * 资产编号;质控部定义编号规则）
     */
    private String assetSn;

    /**
     * 所属公司id;安维99，宁波安联201
     */
    private Long companyId;

    /**
     * 所属部门id
     */
    private Long deptId;

    /**
     * 责任人
     */
    private String charger;

    /**
     * 保管人
     */
    private String keeper;

    /**
     * 资产分类;只有一级分类）
     */
    private String assetType;

    /**
     * 固定资产名称;最多18个字符,前端验证)
     */
    private String name;

    /**
     * 单位
     */
    private String unit;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 规格、型号
     */
    private String model;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 出厂编号
     */
    private String factorySn;

    /**
     * 经销商
     */
    private String dealer;

    /**
     * 采购订单id
     */
    private Long orderId;

    /**
     * 采购价格;单位：分）
     */
    private BigDecimal purchasePrice;

    /**
     * 采购时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseTime;

    /**
     * 采购数量
     */
    private Long purchaseAmount;

    /**
     * 采购金额
     */
    private BigDecimal purchaseValue;

    /**
     * 金额
     */
    private BigDecimal value;

    /**
     * 到货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date arriveTime;

    /**
     * 备注
     */
    private String notes;

    /**
     * 是否为需要检定的仪器
     */
    private Integer isInstrument;

    /**
     * 精度
     */
    private String precisions;

    /**
     * 上次检定时间;仪器）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date inspectTime;

    /**
     * 下次检定时间;仪器）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextInspectTime;

    /**
     * 是否检定
     */
    private Integer isInspected;

    /**
     * 上次检定结果;仪器）
     */
    private String inspectResult;

    /**
     * 检定周期
     */
    private String inspectCycle;

    /**
     * 检定证书存储路径
     */
    private String certPath;

    /**
     * 准用说明
     */
    private String permit;

    /**
     * 是否为虚拟资产;默认否，不纳入日常管理）
     */
    private Integer isVirtual;

    /**
     * 盘点时间
     */
    private Date checkTime;

    /**
     * 有效截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireTime;

    /**
     * 是否是会计学意义上固定资产
     */
    @TableField(exist = false)
    private Integer isAccount;

    /**
     * 是否打印标签
     */
    private Integer isLabelled;

    /**
     * 图片存储路径;数组以;隔开
     */
    private String picPath;

    /**
     * 附件存储路径;数组以;隔开
     */
    private String attPath;

    /**
     * 入库经办人
     */
    private String operator;

    /**
     * 入库类型:;1按订单入库,2直接入库
     */
    private Integer checkinType;

    /**
     * 存储位置
     */
    private String storageLoc;

    /**
     * 存储条件
     */
    private String storageCond;

    /**
     * 测量范围
     */
    private String measureScope;

    /**
     * 准确度等级
     */
    private String accuracyLv;

    /**
     * 定值???
     */
    private String fixedValue;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否移交
     */
    private Integer isTransfer;

    /**
     * 品类
     */
    private Long categoryId;

    /**
     * 申请单号
     */
    private String purchaseCode;

    /**
     * 图片上传：0否，1是
     */
    private Integer uploadPic;

    /**
     * 采购时间
     */
    @TableField(exist = false)
    private String pdfPurchaseTime;

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
     * 资产类型名称
     */
    @TableField(exist = false)
    private String assetTypeName;

    /**
     * 责任人ID
     */
    @TableField(exist = false)
    private Long chargeId;

    /**
     * 录入人
     */
    @TableField(exist = false)
    private Long userId;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String stateName;
}
