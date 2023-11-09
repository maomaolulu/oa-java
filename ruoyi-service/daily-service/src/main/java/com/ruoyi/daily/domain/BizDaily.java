package com.ruoyi.daily.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 日报
 * @author zx->bao
 * @date 2023/4/4 09:01
 */
@TableName("biz_daily")
@Data
public class BizDaily implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 提交人
     */
    private Long userId;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 公司id
     */
    private Long comId;
    /**
     * 打卡id
     */
    private Long dkId;
    /**
     * 今日完成工作
     */
    private String finishWork;
    /**
     * 未完成工作
     */
    private String unfinishedWork;
    /**
     * 需协调工作
     */
    private String needToCoordinate;
    /**
     * 明日待办工作
     */
    private String toDo;
    /**
     * 备注
     */
    private String remark;
    /**
     * 来源：pc端1；小程序2；app端3
     */
    private String source;
    /**
     * 逻辑删
     */
    private Integer delFlag;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 日报所属时间（补日报）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dailyTime;

    @TableField(exist = false)
    private Date startTime;
    @TableField(exist = false)
    private Date endTime;
    @TableField(exist = false)
    private List<BizDailyUser> dailyUser;
}
