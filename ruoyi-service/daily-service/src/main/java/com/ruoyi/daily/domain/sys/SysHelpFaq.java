package com.ruoyi.daily.domain.sys;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 帮助中心
 * Created by WuYang on 2022/8/18 9:54
 */
@TableName("sys_help_faq")
@Data
public class SysHelpFaq {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    /**
     *  标题
     */
    private String title;


}
