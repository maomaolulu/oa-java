package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@Data
public class SysUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Id
    @Excel(name = "用户序号", prompt = "用户编号")
    private Long              userId;
    /**
     * 微信码
     */
    private String wxCode;
    /**
     * app cid
     */
    private String cid;

    /**
     * 公司
     */
    @Transient
    private String company;
    /**
     * 公司id
     */
    @Transient
    private Long companyId;

    /**
     * 是否解绑
     */
    @Transient
    private boolean untie;
    /** 部门ID */
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long              deptId;

    /** 部门负责人 */
    @Excel(name = "部门负责人", type = Type.IMPORT)
    private String              leader;

    /** 自定义审批权限ids */
    private String             otherDeptId;
    /**
     * 自定义数据权限
     */
    private String userPermission;
    @Transient
    private List<String>       otherDeptIds;
    @Transient
    private List<String>       userPermissions;

    /** 部门父ID */
    private Long              parentId;
    /** 企业微信id**/
    private String weixinCompanyId;

    /** 登录名称 */
    @Excel(name = "登录名称")
    private String            loginName;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String            userName;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    private String            email;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String            phonenumber;

    /** 用户性别 */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String            sex;

    /** 用户头像 */
    private String            avatar;

    /** 密码 */
    private String            password;

    /** 盐加密 */
    private String            salt;

    /** 帐号状态（0正常 1停用） */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String            status;

    /** 删除标志（0代表存在 2代表删除） */
    private String            delFlag;

    /** 最后登陆IP */
    @Excel(name = "最后登陆IP", type = Type.EXPORT)
    private String            loginIp;

    /** 最后登陆时间 */
    @Excel(name = "最后登陆时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date              loginDate;

    /** 部门对象 */
    @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT)
    private SysDept           dept;

    private List<SysRole>     roles;

    /** 角色组 */
    private List<Long>            roleIds;

    /** 岗位组 */
    private Long[]            postIds;
    /**
     * 权限字符
     */
    private Set<String>       buttons;
    @Transient
    private Set<String>       buttons2;


    /**
     * 上次登录时间
      */
    @Transient
    private Date loginLast;

    /**
     * 是否优维用户
     */
    @Transient
    private boolean bindUniwe;

    /**
     * 用户名（拼音）
     */
    @Transient
    private List<String> usernamePy;

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    /**
     * 是否直属集团
     * @return
     */
    private int isGroup;

    public int getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(int isLeader) {
        this.isLeader = isLeader;
    }

    /**
     * 是否直属集团的领导（分公司总经理，职能部门主管）
     * @return
     */
    private int isLeader;

    public Date getLoginLast() {
        return loginLast;
    }

    public void setLoginLast(Date loginLast) {
        this.loginLast = loginLast;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getLoginIp()
    {
        return loginIp;
    }

    public void setLoginIp(String loginIp)
    {
        this.loginIp = loginIp;
    }

    public Date getLoginDate()
    {
        return loginDate;
    }

    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate;
    }

    public SysDept getDept()
    {
        if (dept == null)
        {
            dept = new SysDept();
        }
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }


    public List<SysRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<SysRole> roles)
    {
        this.roles = roles;
    }


    public List<Long> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds)
    {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds()
    {
        return postIds;
    }

    public void setPostIds(Long[] postIds)
    {
        this.postIds = postIds;
    }

    public Set<String> getButtons()
    {
        return buttons;
    }

    public void setButtons(Set<String> buttons)
    {
        this.buttons = buttons;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("userId", getUserId())
                .append("deptId", getDeptId()).append("loginName", getLoginName()).append("userName", getUserName())
                .append("email", getEmail()).append("phonenumber", getPhonenumber()).append("sex", getSex())
                .append("avatar", getAvatar()).append("password", getPassword()).append("salt", getSalt())
                .append("status", getStatus()).append("delFlag", getDelFlag()).append("loginIp", getLoginIp())
                .append("loginDate", getLoginDate()).append("createBy", getCreateBy())
                .append("createTime", getCreateTime()).append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime()).append("remark", getRemark()).append("dept", getDept())
                .toString();
    }
}
