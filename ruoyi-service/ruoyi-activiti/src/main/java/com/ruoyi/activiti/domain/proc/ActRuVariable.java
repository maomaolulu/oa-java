package com.ruoyi.activiti.domain.proc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程运行时变量
 * @author zx
 * @date 2021/12/8 15:55
 */
@TableName(value="ACT_RU_VARIABLE")
@Data
public class ActRuVariable implements Serializable {
    @TableId(type = IdType.AUTO,value = "ID_")
    private String id ;
    /**  */
    @TableField("REV_")
    private Integer rev ;
    /**  */
    @TableField("TYPE_")
    private String type ;
    /**  */
    @TableField("NAME_")
    private String name ;
    /**  */
    @TableField("EXECUTION_ID_")
    private String executionId ;
    /**  */
    @TableField("PROC_INST_ID_")
    private String procInstId ;
    /**  */
    @TableField("TASK_ID_")
    private String taskId ;
    /**  */
    @TableField("BYTEARRAY_ID_")
    private String bytearrayId ;
    /**  */
    @TableField("DOUBLE_")
    private Double doubleValue ;
    /**  */
    @TableField("LONG_")
    private Long longValue ;
    /**  */
    @TableField("TEXT_")
    private String text ;
    /**  */
    @TableField("TEXT2_")
    private String text2 ;
}
