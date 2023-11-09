package com.ruoyi.file.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 附件表
 * @author zx
 * @date 2021/12/19 9:31
 */
@Data
@TableName("sys_attachment")
public class SysAttachment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 附件类型1报销凭证2报销附件 */
    private String types ;
    /** 文件名 */
    private String name ;
    /** url */
    private String url ;
    /** 父级id */
    private Long parentId ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createdBy ;
    /** 创建时间 */
    private Date createdTime ;
    /** 更新人 */
    private String updatedBy ;
    /** 更新时间 */
    private Date updatedTime ;
    /**
     * 临时唯一标识
     */
    private String tempId;
    /**
     *
     */
    private String mParentId;
    /**
     * 预览链接
     */
    @TableField(exist = false)
    private String preUrl;
}
