package com.ruoyi.activiti.domain.proc;

import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/**
 * <p>File：BizAudit.java</p>
 * <p>Title: 审批记录</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月9日 下午5:16:02</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class BizAudit
{
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long     id;

    private String   taskId;

    private Integer  result;

    private String   comment;

    private String   procDefKey;

    private String   procName;

    private String   applyer;

    private String   auditor;

    private Long     auditorId;

    private Date     createTime;

    @Transient
    private String   procInstId;

    @Transient
    private Long     businessKey;

    @Transient
    private String[] taskIds;

    @Transient
    private int isCancel;
    @Transient
    private String taskDefKey;
    /**
     * 自定义下一个审批人
     */
    @Transient
    private Long customizedUserId;
}
