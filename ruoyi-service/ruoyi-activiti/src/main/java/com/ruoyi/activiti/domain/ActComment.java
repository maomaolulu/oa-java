package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author zx
 * @date 2022/2/21 15:00
 */
@Data
@TableName("biz_act_comment")
public class ActComment {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 流程实例id */
    private String procInstId ;
    /** 用户id */
    private Long userId ;
    /** 评论内容 */
    private String comment ;
    /** 逻辑删 */
    private String delFlag;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 用户名 */
    @TableField(exist = false)
    private String userName;
}
