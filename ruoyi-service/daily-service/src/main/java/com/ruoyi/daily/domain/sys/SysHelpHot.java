package com.ruoyi.daily.domain.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 热点问题
 * Created by WuYang on 2022/8/18 9:48
 */
@TableName("sys_help_hot")
@Data
public class SysHelpHot {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    /**
     * 回答
     */
    private String answer;
    /**
     * 提问
     */
    private String question;


}
