package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2023/4/17 15:38
 * @Version 1.0
 * @Description 财务申请processkey角色中间表
 */
@TableName("biz_processkey_role")
@Data
public class BizProcesskeyRole implements Serializable {

    private final static long serialVersionUID = 1L;

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 财务申请唯一标志 */
    private String processKey;

    /** 角色id */
    private Integer roleId;
}
