package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 薪资核准
 * @author zx
 * @date 2022/3/8 21:13
 */
@Data
@TypeAlias("biz_salary_approval")
public class BizSalaryApproval implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 标题 */
    private String title ;
    /** 部门id（数据权限） */
    private Long deptId ;
    /** 抄送人 */
    private String cc ;
    /** 审批编号 */
    private String applyCode ;
    /** 入职部门 */
    private Long userDept ;
    /** 姓名 */
    private String name ;
    /**
     * 用户id
     */
    private Long userId ;

    /** 岗位 */
    private String post ;
    /** 学历 */
    private String education ;
    /** 学校 */
    private String school ;
    /** 入职日期 */
    private String onboardingDate ;
    /** 试用期薪资（元） */
    private Double probationSalary ;
    /** 转正薪资（元） */
    private Double positiveSalary ;
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
     * 图片
     */
    @TableField(exist = false)
    private List<SysAttachment> vouchers;
    /**
     * 附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
    /**
     * 抄送人名称
     */
    @TableField(exist = false)
    private String ccName;
    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;
    /**
     * 入职部门名称
     */
    @TableField(exist = false)
    private String userDeptName;
}
