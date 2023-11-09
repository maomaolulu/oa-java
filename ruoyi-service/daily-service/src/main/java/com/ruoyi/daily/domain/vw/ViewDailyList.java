package com.ruoyi.daily.domain.vw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 日报列表视图
 * @author zx
 * @date 2022/1/8 17:27
 * SELECT
 * 	u.user_name ,
 * 	bdu.id,
 * 	bdu.receiver,
 * 	bdu.is_read ,
 * 	bdu.daily_id ,
 * 	bdu.create_time ,
 * 	bdu.update_time
 * FROM
 * 	biz_daily AS bd
 * 	INNER JOIN 	biz_daily_user AS bdu ON bd.id = bdu.daily_id
 * 	INNER JOIN	sys_user AS u ON bd.user_id = u.user_id
 * 	order by `bdu`.`create_time` desc
 */
@TableName("view_daily_list")
@Data
public class ViewDailyList implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 提交人
     */
    private String userName;
    /**
     * 接收人id
     */
    private Long receiver;
    /**
     * 是否已读
     */
    private String isRead;
    /**
     * 日报id
     */
    private Long DailyId;
    private Date createTime;
    private Date updateTime;
    @TableField(exist = false)
    private Date startTime;
    @TableField(exist = false)
    private Date endTime;


}
