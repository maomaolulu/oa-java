package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.domain.SysUserDept;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色与部门关联表 数据层
 * 
 * @author ruoyi
 */
@Repository
public interface SysUserDeptMapper
{
    /**
     * 通过角色ID删除角色和部门关联
     * 
     * @param userId 角色ID
     * @return 结果
     */
    public int deleteUserDeptByUserId(Long userId);

    /**
     * 批量删除角色部门关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserDept(Long[] ids);

    /**
     * 查询部门使用数量
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int selectCountUserDeptByDeptId(Long deptId);

    /**
     * 批量新增角色部门信息
     * 
     * @param userDeptList 角色部门列表
     * @return 结果
     */
    public int batchUserDept(List<SysUserDept> userDeptList);

    /**
     * 获取部门负责人信息
     * @param userId 用户id
     * @return 用户信息
     */
    SysUser selectLeaderByUserId(Long userId);
}
