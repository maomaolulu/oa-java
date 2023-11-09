package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆使用明细
 * @author zx
 * @date 2022/3/14 17:25
 */
@Data
@TableName("biz_reserve_detail")
public class BizReserveDetail implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 用车申请id */
    private Long applyId ;
    /** 车牌号 */
    private String plateNumber ;
    /** 预计出行日期 */
    private Date startDate ;
    /** 预计还车日期 */
    private Date endDate ;
    /** 车辆状态 */
    private String carStatus ;
    /** 用车人 */
    private String carUser ;
    /**
     * 备注
     */
    private String remark;
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
}
