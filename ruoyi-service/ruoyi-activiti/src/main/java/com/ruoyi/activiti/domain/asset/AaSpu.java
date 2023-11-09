package com.ruoyi.activiti.domain.asset;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 库存spu
 * @author zx
 * @date 2021/12/3 11:41
 */
@Data
@Table(name = "aa_spu")
public class AaSpu {
    @Id
    private Long id ;
    /** 公司id */
    private Long companyId ;
    /** 耗材编号 */
    private String spuSn ;
    /** 耗材分类 */
    private Integer spuType ;
    /** 名称 */
    private String name ;
    /** 数量单位 */
    private String unit ;
    /** 保质期;天数） */
    private Integer period ;
    /** 库存短缺限值 */
    private Long shortLimit ;
    /**
     * 是否已检定
     */
    private Integer isInspected;
    /**
     * 规格型号
     */
    private String model;
    /**
     * 是否为制毒、制爆等
     */
    private String hazardType;
    /**
     * 存放条件
     */
    private String storageCond;
    /**
     * 现有库存
     */
    private Integer storageNum;

    /** 公司名称 */
    @Transient
    private String companyName ;
    /**
     * 更新时间
     */
    private Date updateTime ;
    /**
     * 更新人
     */
    private String updateBy ;

}
