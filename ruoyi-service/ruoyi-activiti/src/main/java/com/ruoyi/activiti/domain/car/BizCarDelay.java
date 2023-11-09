package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author 郝佳星
 * Date 2022/6/13 8:30
 **/
@Data
@TableName(value="biz_car_delay")
public class BizCarDelay {
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 次数 */
    private Integer time;
    /** 延迟后时间 */
    private Date delayTime ;
    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;
    /** 审批状态 */
    private String state;
    /** 修改人 */
    private String updateBy;
    /** 审批时间  */
    private Date approvalTime;
    /** 关联关系 */
    private Long aplyRelation;

    /** 备注 */
    private String remark;
    /** 修改人id */
    private long updateById;
    /** 创建人id */
    private long createById;



}
