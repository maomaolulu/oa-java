package com.ruoyi.daily.domain.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 系统版本
 * Created by WuYang on 2022/8/18 9:34
 */
@TableName("sys_help_version")
@Data
public class SysHelpVersion implements Serializable {

    /**
     * 版本id
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    /**
     * 版本号
     */
    private String version;
    /**
     * 标题
     */
    private String title;
    /**
     * 具体内容
     */
    private String items;
    /**
     * 日期
     */
    private LocalDate date;
    /**
     * 公告类型 1 web| 2 app| 3 小程序
     */
    private Integer type;

}
