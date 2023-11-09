package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客服信息对象 quote_customer_service
 * 
 * @author yrb
 * @date 2022-05-06
 */
public class QuoteCustomerService extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 部门id */
    @Excel(name = "部门id")
    private Long departmentId;

    /** 所属部门 */
    @Excel(name = "所属部门")
    private String department;

    /** 姓名所属id */
    @Excel(name = "姓名所属id")
    private Long userId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 电话 */
    @Excel(name = "电话")
    private String telephone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCompanyId(Long companyId) 
    {
        this.companyId = companyId;
    }

    public Long getCompanyId() 
    {
        return companyId;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setDepartmentId(Long departmentId) 
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId() 
    {
        return departmentId;
    }
    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setTelephone(String telephone) 
    {
        this.telephone = telephone;
    }

    public String getTelephone() 
    {
        return telephone;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("companyId", getCompanyId())
            .append("companyName", getCompanyName())
            .append("departmentId", getDepartmentId())
            .append("department", getDepartment())
            .append("userId", getUserId())
            .append("name", getName())
            .append("telephone", getTelephone())
            .append("email", getEmail())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
