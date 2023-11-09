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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

 /**
 * 库存spu;
 * @author : zx
 * @date : 2022-8-17
 */
@ApiModel(value = "库存spu",description = "")
@TableName("aa_spu")
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class AaSpu implements Serializable,Cloneable{
    /** id */
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
    /** 耗材编号 */
    @ExcelProperty(value = "品类编号",index= 1)
    @ApiModelProperty(name = "耗材编号",notes = "")
    private String spuSn ;
    /** 耗材分类 */
    @ExcelIgnore
    @ApiModelProperty(name = "耗材分类",notes = "")
    private Integer spuType ;
    /** 名称 */
    @ExcelProperty(value = "品类名称",index= 2)
    @ApiModelProperty(name = "名称",notes = "")
    private String name ;
    /** 规格型号 */
    @ExcelProperty(value = "规格型号",index= 3)
    @ApiModelProperty(name = "规格型号",notes = "")
    private String model ;
    /** 是否为制毒、制爆等 */
    @ExcelProperty(value = "是否为制毒、制爆等",index= 9)
    @ApiModelProperty(name = "是否为制毒、制爆等",notes = "")
    private String hazardType ;
    /** 存储条件 */
    @ExcelProperty(value = "存储条件",index= 10)
    @ApiModelProperty(name = "存储条件",notes = "")
    private String storageCond ;
    /** 是否已检定 */
    @ExcelIgnore
    @ApiModelProperty(name = "是否已检定",notes = "")
    private Integer isInspected ;
    /** 单价 (采购均价)*/
    @ExcelIgnore
    @ApiModelProperty(name = "单价",notes = "")
    private BigDecimal price ;
    /** 数量单位 */
    @ExcelProperty(value = "单位",index= 6)
    @ApiModelProperty(name = "数量单位",notes = "")
    private String unit ;
    /** 保质期（天数） */
    @ExcelIgnore
    @ApiModelProperty(name = "保质期（天数）",notes = "")
    private Integer period ;
    /** 库存短缺限值 */
    @ExcelIgnore
    @ApiModelProperty(name = "库存短缺限值",notes = "")
    private Long shortLimit ;
    /** 备注 */
    @ExcelProperty(value = "备注",index= 15)
    @ApiModelProperty(name = "备注",notes = "")
    private String notes ;
    /** 创建者 */
    @ExcelProperty(value = "录入人",index= 11)
    @ApiModelProperty(name = "创建者",notes = "")
    private String createBy ;
    /** 创建时间 */
    @ColumnWidth(25)
    @ExcelProperty(value = "录入时间",index= 12)
    @ApiModelProperty(name = "创建时间",notes = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime ;
    /** 上次更新者 */
    @ExcelProperty(value = "维护人",index= 13)
    @ApiModelProperty(name = "上次更新者",notes = "")
    private String updateBy ;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(25)
    @ExcelProperty(value = "维护时间",index= 14)
    @ApiModelProperty(name = "更新时间",notes = "")
    private Date updateTime ;
     /** 当前库存 */
     @ExcelProperty(value = "数量",index= 4)
     @ApiModelProperty(name = "当前库存",notes = "")
     private Integer storageNum ;


     /** 公司名称 */
     @ExcelProperty(value = "公司名称",index= 0)
     @ApiModelProperty(name = "公司名称",notes = "")
     @TableField(exist = false)
     private String companyName;
     /** 资产类型 */
     @ExcelProperty(value = "类别",index= 7)
     @ApiModelProperty(name = "资产类型",notes = "")
     @TableField(exist = false)
     private String spuTypeName;
     /** 采购均价 */
     @ApiModelProperty(name = "采购均价",notes = "")
     @ExcelProperty(value = "采购均价",index= 5)
     @TableField(exist = false)
     private BigDecimal averagePrice;
     /** 是否检定 */
     @ExcelProperty(value = "是否检定",index= 8)
     @ApiModelProperty(name = "是否检定",notes = "")
     @TableField(exist = false)
     private String isInspectedName ;


 }