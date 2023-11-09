package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : zh
 * @date : 2022-02-22
 * @desc : 申请车辆名细
 */
@Data
@Table(name="biz_car_info")
public class BizCarInfo implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 用车申请id */
    private Long carApplyId ;
    /** 用车人 */
    private String carUser ;
    /** 同行人 */
    private String peer ;
    /** 用车事由 */
    private String carReason ;
    /** 出差地（具体到区） */
    private String prefecture ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;

}