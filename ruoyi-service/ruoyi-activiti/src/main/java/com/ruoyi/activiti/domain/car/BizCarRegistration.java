package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : zh
 * @date : 2022-02-21
 * @desc : 车辆管理
 */
@Data
@Table(name="biz_car_registration")
public class BizCarRegistration implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 车牌号 */
    private String plateNumber ;
    /** 车驾号 */
    private String frameNumber ;
    /** 所属公司 */
    private Long companyId ;
    /** 所属部门 */
    private Long deptId ;
    /** 品牌型号 */
    private String carBrand ;
    /** 核载人数 */
    private Integer peopleNumber ;
    /** 车辆属性（0：公车，1：私车） */
    private Integer types ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;
    /** 更新人 */
    private Long updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /** 部门名称 */
    @TableField(exist = false)
    private String deptName ;
    /** 公司名称 */
    @TableField(exist = false)
    private String companyName ;

    /** 车辆所属人 */
    private String carUserName;

    /** 车辆状态;1正常2禁用 */
    private String status ;
    /** 最新里程数 */
    private BigDecimal latestMileage ;
    /** 状态最后变更人 */
    private String statusUpdateUser ;
    /** 状态最后变更时间 */
    private Date statusUpdateTime ;
    /** 状态最后变更原因 */
    private String statusUpdateReason ;
    /**
     * 用车人
     */
    private String carUser;

    /**
     * 预计开始用车时间
     */
    @TableField(exist = false)
    private String goDate;

    /** 车险有效期 */
    private String carInsurancePeriod ;
    /** 上次保养里程 */
    private BigDecimal lastMaintainanceNum ;
    /**
     * 下次保养里程
     */
    @TableField(exist = false)
    private BigDecimal nextMaintainanceNum ;

    public BigDecimal getNextMaintainanceNum() {
        if(this.lastMaintainanceNum==null){
            this.lastMaintainanceNum = BigDecimal.ZERO;
        }
        return this.lastMaintainanceNum.add(new BigDecimal("5000"));
    }

    /**
     * 当天车辆状态
     */
    @TableField(exist = false)
    private String statusNow;
    /**
     * 日期（当前车辆状态）
     */
    @TableField(exist = false)
    private String statusDate;
}