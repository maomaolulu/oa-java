package com.ruoyi.activiti.domain.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 出入库记录
 * @author zx
 * @date 2021/12/2 21:23
 */
@Data
@Table(name = "aa_transcation")
public class AaTranscation implements Serializable {
    @Id
    private Long id ;
    /** 公司id */
    private Long companyId ;
    /** 部门id */
    private Long deptId ;
    /**
     * 物品名称
     */
    private String name;
    /** 物品类型:1资产;2耗材 */
    private Integer itemType ;
    /**
     * 规格型号
     */
    private String model;
    /** 资产编号/耗材编号 */
    private String itemSn ;
    /** asset_id/spu_id */
    private Long identifier ;
    /** 出入库类型:1直接入库;2采购入库,3直接出库,4领用出库,5报废出库,,6退换货出库,7盘盈入库,8盘亏出库 */
    private Integer transType ;
    /** 数量*1 */
    private Integer amount ;
    /** 单位 */
    private String unit ;
    /** 申请人;领用人、报废人） */
    private String applier ;
    /** 经办人 */
    private String operator ;

    /** 创建者 */
    private String              createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String              updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date                updateTime;

    /** 请求参数 */
    @Transient
    private Map<String, Object> params;

    /** 原有库存 */
    private Integer spuAmount ;
}
