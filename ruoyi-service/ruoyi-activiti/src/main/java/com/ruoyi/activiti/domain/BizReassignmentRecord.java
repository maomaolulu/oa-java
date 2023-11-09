package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 转交记录
 * @author zx
 * @date 2022/1/15 10:47
 */
@Data
@TableName("biz_reassignment_record")
public class BizReassignmentRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 流程业务id */
    private String businessKey ;
    /** 任务节点定义 */
    private String taskDefKey ;
    /** 转交发起人 */
    private Long sourceUser ;
    /** 转交接收人 */
    private Long targetUser ;
    /** 转交理由 */
    private String reason ;
    /** 逻辑删 */
    @TableLogic(value = "0",delval = "1")
    private String delFlag ;
    /** 创建时间 */
    private Date createTime ;

}
