package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author : zh
 * @date : 2021-12-31
 * @desc : 静态文件管理表
 */
@Data
@Table(name="sys_status_config")
public class SysStatusConfig implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 文件名 （old ，new）*/
    private String filePathName ;
    /** 样式（默认：蓝色#1384FF，红色：#C50F0F） */
    private String activeColor ;

}