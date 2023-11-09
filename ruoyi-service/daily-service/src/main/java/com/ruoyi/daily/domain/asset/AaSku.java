package com.ruoyi.daily.domain.asset;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

 /**
 * 库存sku;
 * @author : zx
 * @date : 2022-8-23
 */
@ApiModel(value = "库存sku",description = "")
@TableName("aa_sku")
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class AaSku implements Serializable,Cloneable{
    /**  */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(name = "",notes = "null")
    @ExcelIgnore
    private Long id ;
    /** 公司id */
    @ExcelIgnore
    @ApiModelProperty(name = "公司id",notes = "")
    private Long companyId ;
    /** 部门id */
    @ExcelIgnore
    @ApiModelProperty(name = "部门id",notes = "")
    private Long deptId ;
    /** SPU;id */
    @ExcelIgnore
    @ApiModelProperty(name = "SPU",notes = "id")
    private Long spuId ;
    /** 物品状态 */
    @ApiModelProperty(name = "物品状态",notes = "")
    @ExcelIgnore
    private Integer state ;
    /** 操作类型:1入库;2出库 */
    @ApiModelProperty(name = "操作类型:1入库",notes = "2出库")
    @ExcelIgnore
    private Integer operation ;
    /** 耗材名称 */
    @ExcelProperty(value = "物品名称",index= 2)
    @ApiModelProperty(name = "耗材名称",notes = "")
    private String name ;
    /** 制造商 */
    @ExcelProperty(value = "生产厂家",index= 17)
    @ApiModelProperty(name = "制造商",notes = "")
    private String manufacturer ;
    /** 出厂编号（生产批号） */
    @ExcelProperty(value = "生产批号",index= 9)
    @ApiModelProperty(name = "出厂编号",notes = "")
    private String factoryBatch ;
    /** 化学定值 */
    @ExcelProperty(value = "定值",index= 8)
    @ApiModelProperty(name = "化学定值",notes = "")
    private String stableLmt ;
    /** 存储位置 */
    @ExcelProperty(value = "定存储位置值",index= 10)
    @ApiModelProperty(name = "存储位置",notes = "")
    private String storageLoc ;
    /** 金额 */
    @ExcelIgnore
    @ApiModelProperty(name = "金额",notes = "")
    private BigDecimal value ;
    /** 数量 */
    @ExcelProperty(value = "入库数量",index= 4)
    @ApiModelProperty(name = "数量",notes = "")
    private Long amount ;
    /** 经销商 （供应商）*/
    @ExcelProperty(value = "供应商",index= 16)
    @ApiModelProperty(name = "经销商",notes = "")
    private String dealer ;
    /** 采购订单id */
    @ExcelIgnore
    @ApiModelProperty(name = "采购订单id",notes = "")
    private String orderId ;
    /** 采购价格 */
    @ExcelIgnore
    @ApiModelProperty(name = "采购价格",notes = "")
    private BigDecimal purchasePrice ;
    /** 采购时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "采购时间",index= 12)
    @ColumnWidth(25)
    @ApiModelProperty(name = "采购时间",notes = "")
    private Date purchaseTime ;
    /** 到货时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "到货日期",index= 15)
    @ColumnWidth(25)
    @ApiModelProperty(name = "到货时间",notes = "")
    private Date arriveTime ;
    /** 备注 */
    @ApiModelProperty(name = "备注",notes = "")
    @ExcelProperty(value = "备注",index= 21)
    private String notes ;
    /** 是否已检定 */
    @ExcelIgnore
    @ApiModelProperty(name = "是否已检定",notes = "")
    private Integer isInspected ;
    /** 上次检定时间 */
    @ColumnWidth(25)
    @ExcelProperty(value = "检定日期",index= 18)
    @ApiModelProperty(name = "上次检定时间",notes = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date inspectTime ;
    /** 检定结果 */
    @ExcelProperty(value = "检定结果",index= 19)
    @ApiModelProperty(name = "检定结果",notes = "")
    private String inspectResult ;
    /** 检定周期 */
    @ExcelProperty(value = "检定周期",index= 20)
    @ApiModelProperty(name = "检定周期",notes = "")
    private String inspectCycle ;
    /** 准用说明 */
    @ExcelIgnore
    @ApiModelProperty(name = "准用说明",notes = "")
    private String permit ;
    /** 责任人 */
    @ExcelIgnore
    @ApiModelProperty(name = "责任人",notes = "")
    private String charger ;
    /** 保管人 */
    @ExcelIgnore
    @ApiModelProperty(name = "保管人",notes = "")
    private String keeper ;
    /** 入库人 */
    @ExcelIgnore
    @ApiModelProperty(name = "入库人",notes = "")
    private String operator ;
    /** 创建者 */
    @ExcelIgnore
    @ApiModelProperty(name = "创建者",notes = "")
    private String createBy ;
    /** 创建时间 */
    @ExcelIgnore
    @ApiModelProperty(name = "创建时间",notes = "")
    private Date createTime ;
    /** 上次更新者 */
    @ExcelIgnore
    @ApiModelProperty(name = "上次更新者",notes = "")
    private String updateBy ;
    /** 更新时间 */
    @ExcelIgnore
    @ApiModelProperty(name = "更新时间",notes = "")
    private Date updateTime ;


     /** 公司名称 */
     @ApiModelProperty(name = "公司名称",notes = "")
     @TableField(exist=false)
     @ExcelProperty(value = "公司名称",index= 0)
     private String companyName ;
     /** 资产类别名称 */
     @ExcelIgnore
     @ApiModelProperty(name = "资产类别名称",notes = "")
     @TableField(exist=false)
     private String spuTypeName ;
     /** 资产类别 */
     @ExcelIgnore
     @ApiModelProperty(name = "资产类别",notes = "")
     @TableField(exist=false)
     private String spuType ;
     /** 是否已检定 */
     @ExcelProperty(value = "是否已检定",index= 6)
     @ApiModelProperty(name = "是否已检定",notes = "")
     @TableField(exist=false)
     private String isInspectedName ;
     /** 规格型号 */
     @ExcelProperty(value = "规格型号",index= 3)
     @ApiModelProperty(name = "规格型号",notes = "")
     @TableField(exist=false)
     private String model ;
     /** 单位 */
     @ExcelProperty(value = "物品单位",index= 5)
     @ApiModelProperty(name = "单位",notes = "")
     @TableField(exist=false)
     private String unit ;
     /** 品类编号 */
     @ExcelProperty(value = "品类编号",index= 1)
     @ApiModelProperty(name = "品类编号",notes = "")
     @TableField(exist=false)
     private String spuSn ;
     /** 是否为制毒、制爆等 */
     @TableField(exist=false)
     @ExcelProperty(value = "是否为制毒、制爆等",index= 7)
     @ApiModelProperty(name = "是否为制毒、制爆等",notes = "")
     private String hazardType ;
     /** 存储条件 */
     @ApiModelProperty(name = "存储条件",notes = "")
     @ExcelProperty(value = "存储条件",index= 11)
     @TableField(exist=false)
     private String storageCond ;
     /** 采购单价 */
     @ApiModelProperty(name = "采购单价",notes = "")
     @ExcelProperty(value = "采购单价",index= 13)
     @TableField(exist=false)
     private BigDecimal purchasePriceSingle ;
     /** 采购总额 */
     @ExcelProperty(value = "采购总额",index= 14)
     @ApiModelProperty(name = "采购总额",notes = "")
     @TableField(exist=false)
     private BigDecimal valueTotal ;
     @ExcelIgnore
     @TableField(exist=false)
     private String startDate ;
     @ExcelIgnore
     @TableField(exist=false)
     private String endDate ;
     /**
      * 物品类型
      */
     @ExcelIgnore
     @TableField(exist = false)
     private String itemTypeName = "流动资产";
     /** 采购均价 */
     @TableField(exist = false)
     @ExcelIgnore
     private BigDecimal averagePrice;
     /**
      * 出入库前耗材原有数量
      */
     @TableField(exist = false)
     @ExcelIgnore
     private Integer spuAmount;
     /**
      * 物品类型
      */
     @ExcelIgnore
     @TableField(exist = false)
     private Integer itemType = 2;
     /**
      * 出入库类型:1直接入库,2采购入库,3直接出库,4领用出库,5报废出库,6退换货出库,7盘盈入库,8盘亏出库
      */
     @ExcelIgnore
     @TableField(exist = false)
     private String transTypeName;




}