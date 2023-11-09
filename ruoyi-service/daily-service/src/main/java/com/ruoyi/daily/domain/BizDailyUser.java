package com.ruoyi.daily.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 日报用户关联
 * @author zx
 * @date 2022/1/8 16:28
 */
@TableName("biz_daily_user")
@Data
public class BizDailyUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 日报id */
    private Long dailyId ;
    /** 接收人 */
    private Long  receiver ;
    /** 是否已读 */
    private String isRead ;
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
