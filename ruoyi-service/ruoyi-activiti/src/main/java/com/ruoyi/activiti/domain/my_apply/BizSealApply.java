package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用印申请
 * @author zx
 * @date 2022/1/12 17:15
 */
@Data
@TableName("biz_seal_apply")
public class BizSealApply implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 标题 */
    private String title;
    /** 抄送人 */
    private String cc ;
    /** 审批编号 */
    private String applyCode ;
    /** 部门id（数据权限）*/
    private Long deptId;
    /** 用印部门 */
    private Long  userDept ;
    /** 用印人 */
    private Long sealUser ;
    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date stampDate ;
    /** 用印文件名称 */
    private String document ;
    /** 文件份数 */
    private Integer documentNum ;
    /** 文件类别 */
    private String documentType ;
    @TableField(exist = false)
    private String documentTypeName ;
    /** 印章类型 */
    private String sealType ;
    @TableField(exist = false)
    private String sealTypeName;
    /** 备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /**
     * 用印申请凭证
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 用印申请附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
    /** 抄送人名称 */
    @TableField(exist = false)
    private String ccName;
    /** 申请人公司名称 */
    @TableField(exist = false)
    private String deptName ;
    /** 创建人名称 */
    @TableField(exist = false)
    private String createByName ;
    /** 申请人部门名称 */
    @TableField(exist = false)
    private String companyName;
    /**隶属部门名称 */
    @TableField(exist = false)
    private String subjectionDeptName;
    /**隶属公司名称 */
    @TableField(exist = false)
    private String subjectionCompanyName;
    /** 用印人 */
    @TableField(exist = false)
    private String sealUserName ;
}
