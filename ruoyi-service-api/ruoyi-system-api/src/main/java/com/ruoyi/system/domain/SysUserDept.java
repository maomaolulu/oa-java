package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和部门关联 sys_user_dept
 * 
 * @author ruoyi
 */
public class SysUserDept
{
    /** 角色ID */
    private Long userId;
    
    /** 部门ID */
    private Long deptId;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long roleId)
    {
        this.userId = roleId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .toString();
    }
}
