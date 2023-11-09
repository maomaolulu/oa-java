package com.ruoyi.daily.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.file.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 拜访打卡详情
 * @author zh
 * @date 2023-03-30
 */
@TableName("daily_visit_record_info")
@Data
public class DailyVisitRecordInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 拜访打卡id */
    private Long visitRecordId ;
    /** 文件id */
    private Long parentId ;
    /** 工厂 */
    private String factory ;
    /** 打卡地址 */
    private String address ;
    /** 打卡ip */
    private String ip ;
    /** 打卡时间 */
    private Date clockInTime ;
    /** 打卡时间str */
    @TableField(exist = false)
    private String clockInTimeStr ;
    /** 备注 */
    private String remark ;
    /** 附件 */
    @TableField(exist = false)
    private List<SysAttachment> files  ;
    /** 逻辑删 */
    private Integer delFlag ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新时间 */
    private Date updateTime ;
}
