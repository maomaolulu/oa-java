package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 部门管理 服务层
 *
 * @author ruoyi
 */
public interface ISysDeptService {
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptListWithParent(SysDept dept);

    /**
     * 查询部门管理数据(无过滤权限)
     *
     * @param dept
     * @return
     */
    public List<SysDept> selectDeptList2(SysDept dept);
    /**
     * 查询部门管理数据(无过滤权限)Map
     *
     * @param dept
     * @return
     */
    public List<SysDept> selectDeptListMap(SysDept dept);

    /**
     * 查询部门管理树
     *
     * @param dept 部门信息
     * @return 所有部门信息
     */
    public List<Ztree> selectDeptTree(SysDept dept);

    /**
     * 根据角色ID查询菜单
     *
     * @param role 角色对象
     * @return 菜单列表
     */
    public List<Ztree> roleDeptTreeData(SysRole role);

    /**
     * 查询部门人数
     *
     * @param parentId 父部门ID
     * @return 结果
     */
    public int selectDeptCount(Long parentId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    public String checkDeptNameUnique(SysDept dept);

    /**
     * 根据角色ID查询部门编号
     *
     * @param roleId 角色编号
     * @return 部门编号
     */
    public Set<String> roleDeptIds(Long roleId);

    /**
     * 查询部门
     *
     * @param roleIds 角色数组
     * @return
     */
    List<SysDept> selectDeptAudit(List<Long> roleIds);

    /**
     * 查询部门所在公司
     *
     * @param deptId
     * @return
     */
    Map<String, Object> getBelongCompany(Long deptId);

    /**
     * 查询部门所属一级部门
     *
     * @param deptId
     * @return
     */
    Map<String, Object> getFirstDept(Long deptId);

    /**
     * 查询部门所在公司
     *
     * @param deptId
     * @return
     */
    String getDeptNameAll(Long deptId);

    /**
     * 查询角色自定义权限部门信息
     *
     * @return
     */
    List<SysDept> listRole(List<String> list);

    /**
     * pc端抄送专用
     *
     * @param sysDept
     * @return
     */
    List<Map<String, Object>> selectDeptList3(SysDept sysDept);

    /**
     * 获取树形结构
     * @return
     */
    Map<Integer,Object> getTreeAll();

    /**
     * 获取树形结构(有权限)
     * @return
     */
    Map<Integer,Object> getTreeAllRole(SysDept sysDept);

    /**
     * 获取公司列表
     *
     * @return 集合
     */
    List<SysDept> selectCompanyList(SysDept sysDept);
}
