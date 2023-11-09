package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : zh
 * @date : 2021-02-10
 * @desc : 采购预提交
 */
@Data
@Table(name="biz_anticipate_goods")
public class BizAnticipateGoods implements Serializable{
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 物品名称 */
    private String name ;
    /** 物品数量 */
    private Integer amount ;
    /** 物品单价 */
    private BigDecimal price ;
    /** 单位 */
    private String unit ;
    /** 物品规格型号 */
    private String model ;
    /** 费用归属部门 */
    private Long ascriptionDept ;
    /** 库存id */
    private Long spuId ;
    /** 物品类型:1办公用品2采样耗材,3劳保用品,4实验室试剂耗材,5仪器设备,6仪器设备检定,7仪器设备维修及保养,8其他 */
    private Integer itemType ;
    /** 登记编号 */
    private String goodsType ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 资产类型：0：固定资产，1：流动资产 */
    private Integer assetType ;
    /** 是否已检定：0：未鉴定，1：已检定 */
    private Integer isInspected ;
    /** 物品编号 */
    private String spuSn ;
    /** 物品使用部门 */
    private Long usages  ;

}