package com.ruoyi.daily.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 出入库记录
 * @author zx
 * @date 2022-08-08 15:02:35
 */
@TableName("aa_transcation")
@Data
public class AaTranscation implements Serializable {
  private static final Long serialVersionUID = 1L;
  @TableId(type = IdType.AUTO)
  private Long id;
  /**
   * 公司id
   */
  private Long companyId;
  /**
   * 部门id
   */
  private Long deptId;
  /**
   * 物品名称
   */
  private String name;
  /**
   * 规格型号
   */
  private String model;
  /**
   * 物品类型：1资产2耗材
   */
  private Integer itemType;
  /**
   * 资产编号/耗材编号
   */
  private String itemSn;
  /**
   * asset_id/sku_id
   */
  private Long identifier;
  /**
   * 出入库类型:1直接入库,2采购入库,3直接出库,4领用出库,5报废出库,6退换货出库,7盘盈入库,8盘亏出库
   */
  private Integer transType;
  /**
   * 出入库前耗材原有数量
   */
  private Integer spuAmount;
  /**
   * 数量
   */
  private Integer amount;
  /**
   * 单位
   */
  private String unit;
  /**
   * 是否退换货
   */
  private Integer isReturn;
  /**
   * 申请人（领用人、报废人）
   */
  private String applier;
  /**
   * 经办人
   */
  private String operator;
  /**
   * 创建时间
   */
  @JsonFormat(timezone = "GMT+8")
  private Date createTime;
  /**
   * 创建人
   */
  private String createBy;
  /**
   * 更新人
   */
  private String updateBy;
  /**
   * 更新时间
   */
  @JsonFormat(timezone = "GMT+8")
  private Date updateTime;

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
  

}
