package com.ruoyi.daily.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统协议
 * @author zx
 * @date 2022/1/12 13:46
 */
@Data
@TableName("sys_protocol")
public class SysProtocol implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 内容 */
    private String content ;
    /** 是否启用 */
    private String isActive ;
    /** 类型 */
    private String types ;
    /** 创建时间 */
    private Date createTime ;
    /**
     * 是否已读
     */
    @TableField(exist = false)
    private String isRead;
}
