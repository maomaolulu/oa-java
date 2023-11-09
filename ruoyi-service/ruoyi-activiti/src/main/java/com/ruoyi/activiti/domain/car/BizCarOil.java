package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zx
 * @date 2022/3/4 17:17
 */
@Data
@TableName("biz_car_oil")
public class BizCarOil implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 申请id */
    private Long applyId ;
    /** 加油里程;1） */
    private Double fuelMileage ;
    /** 加油金额;1） */
    private Double fuelMoney ;
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
