package com.ruoyi.socket.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import lombok.NoArgsConstructor;

/**
 * 项目留言对象 abu_project_note
 * 
 * @author yrb
 * @date 2023-04-06
 */
@Data
@TableName("abu_project_note")
public class AbuProjectNote implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String projectId;

    /** 留言内容 */
    @Excel(name = "留言内容")
    private String note;

    /** 提交用户 */
    @Excel(name = "提交用户")
    private Long userId;

    /** 提交时间 */
    @Excel(name = "提交时间")
    private Date createTime;
}
