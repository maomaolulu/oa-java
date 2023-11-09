package com.ruoyi.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 静态文件
 * @author zx
 * @date 2022/1/7 17:47
 */
@Data
@TableName("sys_static_img")
public class SysStaticImg implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 文件名 */
    private String name ;
    /** 原路径 */
    private String path ;
    /** bucket */
    private String types ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /**
     * 预览路径
     */
    @TableField(exist = false)
    private String url;
}
