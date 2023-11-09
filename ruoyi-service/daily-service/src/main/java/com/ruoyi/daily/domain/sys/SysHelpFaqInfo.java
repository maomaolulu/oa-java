package com.ruoyi.daily.domain.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 帮助中心详情
 * Created by WuYang on 2022/8/18 9:55
 */
@TableName("sys_help_faq_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysHelpFaqInfo {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id ;
    /**
     * fid(联表)
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long fid;
    /**
     * 回答
     */
    private String answer;
    /**
     * 提问
     */
    private String question;
}
