package com.ruoyi.daily.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 固定资产
 *
 * @author zx
 * @date 2022/3/15 17:00
 */
@Data
@TableName("aa_asset")
public class Asset implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存编号;12位数字)
     */
    private String labelCode;

    /**
     * 状态:1在用;2准用,3待修,4维修中,5送检中,6报废,7外借,8封存 ,10库存
     */
    private Integer state;

    /**
     * 资产编号;质控部定义编号规则）
     */
    private String assetSn;

    /**
     * 所属公司id
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
     * 资产分类
     */
    private Integer assetType;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inspectTime;

    /**
     * 下次检定时间;仪器）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextInspectTime;

    /**
     * 是否已检定
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
    @JsonFormat(pattern = "yyyy-MM-dd")
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
     * 是否已打印标签
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
     * 维护人;最近一次)
     */
    private String updateBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8")
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
     * 出入库类型:1直接入库,2采购入库,3直接出库,4领用出库,5报废出库,6退换货出库,7盘盈入库,8盘亏出库
     */
    @TableField(exist = false)
    private Integer transType;
    /**
     * 资产类型名称
     */
    @TableField(exist = false)
    private String assetTypeName;
    /**
     * 物品状态名称
     */
    @TableField(exist = false)
    private String stateName;
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
     * 是否已检定
     */
    @TableField(exist = false)
    private String isInspectedName;
    /**
     * 出入库类型
     */
    @TableField(exist = false)
    private String transTypeName;
    /**
     * 物品类型
     */
    @TableField(exist = false)
    private String itemTypeName = "固定资产";
    /**
     * 物品类型
     */
    @TableField(exist = false)
    private Integer itemType = 1;

    /**
     * 图片存储路径;数组以;隔开
     */
    @TableField(exist = false)
    private List<String> picPathList;
    /**
     * 附件存储路径;数组以;隔开
     */
    @TableField(exist = false)
    private List<String> attPathList;
    /**
     * 检定证书存储路径
     */
    @TableField(exist = false)
    private List<String> certPathList;

    /**
     * 大分类key 1采样2实验室
     */
    @TableField(exist = false)
    private String deviceType;
    /**
     * 小分类key
     */
    @TableField(exist = false)
    private String dictKey;

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
}
