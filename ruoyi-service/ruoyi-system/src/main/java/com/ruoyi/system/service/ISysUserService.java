package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;

import java.util.List;
import java.util.Set;

/**
 * 用户 业务层
 *
 * @author ruoyi
 */
public interface ISysUserService {
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList2(SysUser user);

    /**
     * 根据条件分页查询已分配用户角色列表
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
     * @param userName 用户名(中文名)
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 查询用户
     * @param userName
     * @return
     */
//    public SysUser getUserByLoginName(String userName);

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
     * @throws Exception 异常
     */
    public int deleteUserByIds(String ids) throws Exception;

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 保存登录信息
     *
     * @param user
     * @return
     */
    public int updateUser2(SysUser user);

    /**
     * 修改用户详细信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserInfo(SysUser user);

    /**
     * 修改用户密码信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int resetUserPwd(SysUser user);

    /**
     * 校验用户名称是否唯一
     *
     * @param loginName 登录名称
     * @return 结果
     */
    public String checkLoginNameUnique(String loginName);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkEmailUnique(SysUser user);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    public String selectUserRoleGroup(Long userId);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userId 用户ID
     * @return 结果
     */
    public String selectUserPostGroup(Long userId);

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);

    /**
     * 用户状态修改
     *
     * @param user 用户信息
     * @return 结果
     */
    public int changeStatus(SysUser user);

    /**
     * 查询拥有当前角色的所有用户
     *
     * @param roleIds
     * @return
     * @author zmr
     */
    public Set<Long> selectUserIdsHasRoles(Long[] roleIds);

    /**
     * 查询所有当前部门中的用户
     *
     * @param deptIds
     * @return
     * @author zmr
     */
    public Set<Long> selectUserIdsInDepts(Long[] deptIds);

    /**
     * 绑定解绑微信\绑定app-cid
     *
     * @param sysUser
     * @return
     */
    R editSaveWx(SysUser sysUser);

    /**
     * 修改用户头像
     *
     * @param sysUser
     * @return
     */
    int updateAvatar(SysUser sysUser);

    List<SysUser> selectUserIdsInDept(Long deptId);

    /**
     * 通过用户信息获取部门主管信息
     *
     * @param userId 用户id
     * @return result
     */
    SysUser selectLeaderByUserId(Long userId);

    /**
     * 获取公司下的所有人员
     *
     * @param companyId 公司ID
     * @return 集合
     */
    List<SysUser> selectUsersBelongCompany(Long companyId);
}
