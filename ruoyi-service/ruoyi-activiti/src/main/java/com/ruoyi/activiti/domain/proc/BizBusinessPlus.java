package com.ruoyi.activiti.domain.proc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_business")
public class BizBusinessPlus implements Serializable
{
    private static final long serialVersionUID = -7562556845627977390L;

    @TableId(type = IdType.AUTO)
    private Long              id;

    private String            title;

    private Long              userId;

    private String            tableId;

    private String            procDefId;
    private String            procDefKey;

    private String            procInstId;

    // 流程名称
    private String            procName;

    // 当前任务节点名称
    private String            currentTask;

    private String            applyer;

    private Integer           status;

    private Integer           result;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date              applyTime;

    private Boolean           delFlag;
    @TableField(exist = false)
    private String searchValue;
    /**
     * 详情
     */
    @TableField(exist = false)
    private List<Map<String,Object>> detailMap;

    /**
     * 申请编号
     */
    @TableField(exist = false)
    private String applyCode;
    /**
     * 当前审批人
     */
    private String auditors;

    /**
     * 审批意见
     */
    @TableField(exist = false)
    private String comment;

    @TableField(exist = false)
    private Date              startTime;
    @TableField(exist = false)
    private Date              endTime;
    @TableField(exist = false)
    private Long companyId;
    @TableField(exist = false)
    private Long deptId;

    /**
     * 最后审批人
     */
    private String lastAuditor;
    /**
     * 最后审批时间
     */
    private Date lastAuditorTime;


}
