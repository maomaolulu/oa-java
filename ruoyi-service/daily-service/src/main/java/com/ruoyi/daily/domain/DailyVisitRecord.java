package com.ruoyi.daily.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 拜访打卡
 * @author zh
 * @date 2023-03-30
 */
@TableName("daily_visit_record")
@Data
public class DailyVisitRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 部门id */
    private Long deptId ;
    /** 打卡日期 */
    private Date clockInDate ;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private Integer delFlag ;
    /** 创建人（打卡人） */
    private Long userId ;
    /** 创建人名称（打卡人名称） */
    private String createByName ;
    /** 更新人 */
    private String updateBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新时间 */
    private Date updateTime ;
    /** 打卡详情 */
    @TableField(exist = false)
    private List<DailyVisitRecordInfo> dailyVisitRecordInfos;
    /** 业务员所属公司 */
    private String company;
}
