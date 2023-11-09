package com.ruoyi.activiti.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 用印申请
 * @author zx
 * @date 2022/1/13 10:10
 */
@Data
public class SealApplyVo {
    private Long id ;
    /**
     * 标题
     */
    private String title;
    /**
     * 当前审批人
     */
    private String auditors;
    /**
     * 审批状态
     */
    private Integer status;
    /**
     * 审批结果
     */
    private Integer result;
    /** 审批编号 */
    private String applyCode ;
    /** 部门id（数据权限）*/
    private Long deptId;
    /** 用印部门 */
    private String  userDept ;
    /** 用印人 */
    private String sealUser ;
    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date stampDate ;
    /** 用印文件名称 */
    private String document ;
    /** 文件份数 */
    private Integer documentNum ;
    /** 文件类别 */
    private String documentTypeName ;
    private String documentType ;
    /** 印章类型 */
    private String sealTypeName;
    private String sealType;
    /** 创建时间 */
    private Date createTime ;
    /**
     * 流程唯一id
     */
    private String procInstId;
}
