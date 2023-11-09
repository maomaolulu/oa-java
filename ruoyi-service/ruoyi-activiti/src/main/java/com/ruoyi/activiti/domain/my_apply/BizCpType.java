package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/6/13 15:04
 * @Version 1.0
 * @Description 合同类型、项目类型
 */
@Data
@TableName("biz_cp_type")
public class BizCpType implements Serializable {

    private final static long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型 */
    private String type;

    /** 父id */
    private Long parentId;

    @TableField(exist = false)
    private List<BizCpType> children;
}
