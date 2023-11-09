package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 耗材盘点明细
 * @author zx
 * @date 2022/3/29 14:08
 */
@Data
@TableName("biz_consumables_inventory_detail")
public class BizConsumablesInventoryDetail implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 耗材品类id */
    private Long spuId ;
    /** 加减数量 */
    private Integer inventoryNum ;
    /** 申请id */
    private Long applyId ;
    /** 物品名称 */
    private String name ;
    /** 规格型号 */
    private String model ;
    /** 是否检定 */
    private String isInspected ;
    /** 库存数量 */
    private String storageNum ;
    /** 单位 */
    private String unit ;
}
