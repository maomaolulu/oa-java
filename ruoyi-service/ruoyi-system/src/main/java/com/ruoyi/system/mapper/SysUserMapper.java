package com.ruoyi.system.mapper;

import com.ruoyi.common.core.dao.BaseMapper;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 用户表 数据层
 *
 * @author ruoyi
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser>
{
    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询未已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByLoginName(String userName);

    /**
     * 通过用户名查询用户(中文名)
     *
     * @param userName 用户名（中文名）
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过手机号码查询用户
     *
     * @param phoneNumber 手机号码
     * @return 用户对象信息
     */
    public SysUser selectUserByPhoneNumber(String phoneNumber);

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    public SysUser selectUserByEmail(String email);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户自定义审批权限
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserAudit(SysUser user);

    /**
     * 记录登录信息
     * @param user
     * @return
     */
    public int updateUser2(SysUser user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 校验用户名称是否唯一
     *
     * @param loginName 登录名称
     * @return 结果
     */
    public int checkLoginNameUnique(String loginName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public SysUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public SysUser checkEmailUnique(String email);

    /**
     * 查询拥有当前角色的所有用户编号
     * @param roleIds 角色编号
     * @return
     * @author zmr
     */
    public Set<Long> selectUserIdsHasRoles(Long[] roleIds);
    /**
     * 查询拥有当前角色的所有用户编号
     * @param deptIds 部门编号
     * @return
     * @author zmr
     */
    public Set<Long> selectUserIdsInDepts(Long[] deptIds);

     /**
     * 查询某个部门及其子部门的用户
     * @param deptId_anc 部门编号
     * @param deptId 部门编号
     * @return
     * @author zx
     */
    List<SysUser> selectUserIdsInDept(@Param("deptId_anc")String deptId_anc,@Param("deptId")String deptId);



    /**
     * 查询包含某个部门审批权限的用户
     * @param deptId
     * @return
     */
    @Select("select u.user_id as userId,u.other_dept_id as otherDeptId,u.user_permission as userPermission FROM sys_user u WHERE u.other_dept_id like #{deptId} and u.status = '0' and u.del_flag = 0 ")
    List<SysUser> selectAudit(@Param("deptId") String deptId);

    /**
     * 查询包含某个部门审批权限的角色
     * @param deptId
     * @return
     */
    @Select("SELECT " +
            " r.role_id as roleId, " +
            " r.role_dept as roleDept  " +
            "FROM " +
            " sys_role r  " +
            "WHERE " +
            " r.role_dept LIKE #{deptId}  " +
            "AND r.STATUS = '0'  " +
            "AND r.del_flag = 0")
    List<SysRole> selectAuditRole(@Param("deptId") String deptId);

    /**
     * 更新角色部门字段
     * @param roleId
     * @param roleDept
     * @return
     */
    @Update("update sys_role set role_dept = #{roleDept} WHERE role_id = #{roleId} ")
    int updateRoleDept(@Param("roleId") Integer roleId,@Param("roleDept")String roleDept);

    /**
     * 查询包含某个部门数据权限的用户
     * @param deptId
     * @return
     */
    @Select("select u.user_id as userId,u.other_dept_id as otherDeptId,u.user_permission as userPermission FROM sys_user u WHERE u.user_permission like #{deptId} and u.status = '0' and u.del_flag = 0 ")
    List<SysUser> selectPerm(@Param("deptId")String deptId);

    /**
     * 新增权限
     * @return
     */

    @Insert("${sqlStr}")
    int addAuth(@Param(value = "sqlStr") String sqlStr);

    /**
     * 获取本部门及子部门id集合
     * @param sqlStr
     * @return List<Long>
     */
    @Select("${sqlStr}")
    List<String> getDeptIdAndChildrenId(@Param(value = "sqlStr") String sqlStr);

    /**
     * 删除原权限
     * @param sqlStr
     * @return
     */
    @Delete("${sqlStr}")
    int deleteAuth(@Param(value = "sqlStr") String sqlStr);
}
