package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.redis.annotation.RedisCache;
import com.ruoyi.common.redis.annotation.RedisEvict;
import com.ruoyi.common.redis.annotation.RedisEvict2;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleDeptMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.utils.DeptTree;
import com.ruoyi.system.vo.DeptTreeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * 部门管理 服务实现
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class SysDeptServiceImpl implements ISysDeptService {
    @Autowired
    private RedisService redisService;
    private final SysDeptMapper deptMapper;
    private final SysRoleDeptMapper roleDeptMapper;
    private final SysUserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;
    private final static String ZERO_STR = "0";


    @Autowired
    public SysDeptServiceImpl(SysDeptMapper deptMapper, SysRoleDeptMapper roleDeptMapper, SysUserMapper userMapper) {
        this.deptMapper = deptMapper;
        this.roleDeptMapper = roleDeptMapper;
        this.userMapper = userMapper;
    }

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理数据,若只有一个部门则把父级也查出来
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptListWithParent(SysDept dept) {
        List<SysDept> sysDepts = deptMapper.selectDeptList(dept);
        if (sysDepts.size() == 1) {
            SysDept sysDept = deptMapper.selectDeptById(sysDepts.get(0).getParentId());
            sysDepts.add(sysDept);
        }
        return sysDepts;
    }


    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @RedisCache(key = "dept_enabled2", fieldKey = "1")
    public List<SysDept> selectDeptList2(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理数据Map
     *
     * @param dept 部门信息
     * @return 部门信息集合Map
     */
    @Override
    public List<SysDept> selectDeptListMap(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理树
     *
     * @param dept 部门信息
     * @return 所有部门信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<Ztree> selectDeptTree(SysDept dept) {
        List<SysDept> deptList = deptMapper.selectDeptList(dept);
        List<Ztree> ztrees = initZtree(deptList);
        return ztrees;
    }

    /**
     * 根据角色ID查询部门（数据权限）
     *
     * @param role 角色对象
     * @return 部门列表（数据权限）
     */
    @Override
    public List<Ztree> roleDeptTreeData(SysRole role) {
        Long roleId = role.getRoleId();
        List<Ztree> ztrees = new ArrayList<Ztree>();
        List<SysDept> deptList = selectDeptList(new SysDept());
        if (StringUtils.isNotNull(roleId)) {
            List<String> roleDeptList = deptMapper.selectRoleDeptTree(roleId);
            ztrees = initZtree(deptList, roleDeptList);
        } else {
            ztrees = initZtree(deptList);
        }
        return ztrees;
    }

    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList) {
        return initZtree(deptList, null);
    }

    /**
     * 对象转部门树
     *
     * @param deptList     部门列表
     * @param roleDeptList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList, List<String> roleDeptList) {

        List<Ztree> ztrees = new ArrayList<Ztree>();
        boolean isCheck = StringUtils.isNotNull(roleDeptList);
        for (SysDept dept : deptList) {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
                Ztree ztree = new Ztree();
                ztree.setId(dept.getDeptId());
                ztree.setpId(dept.getParentId());
                ztree.setName(dept.getDeptName());
                ztree.setTitle(dept.getDeptName());
                if (isCheck) {
                    ztree.setChecked(roleDeptList.contains(dept.getDeptId() + dept.getDeptName()));
                }
                ztrees.add(ztree);
            }
        }
        return ztrees;
    }

    /**
     * 查询部门人数
     *
     * @param parentId 部门ID
     * @return 结果
     */
    @Override
    public int selectDeptCount(Long parentId) {
        SysDept dept = new SysDept();
        dept.setParentId(parentId);
        return deptMapper.selectDeptCount(dept);
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    @RedisEvict(key = "dept_tree_all", fieldKey = "1")
    @RedisEvict2(key = "dept_enabled2", fieldKey = "1")
    public int deleteDeptById(Long deptId) {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @RedisEvict(key = "dept_tree_all", fieldKey = "1")
    @RedisEvict2(key = "dept_enabled2", fieldKey = "1")
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept dept) {

        try {
            if (dept.getParentId() > 0) {

                SysDept info = deptMapper.selectDeptById(dept.getParentId());
                // 如果父节点不为"正常"状态,则不允许新增子节点
                if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
                    throw new BusinessException("部门停用，不允许新增");
                }
                dept.setAncestors(info.getAncestors() + dept.getParentId() + ",");
            }
            if (dept.getParentId() == 0) {
                dept.setAncestors("0,");
            }
            deptMapper.insertDept(dept);
            updatePerm(dept);
            return 1;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

    }

    // 删除旧数据
    private void deletePerm(List<SysUser> sysUsersAudit, List<SysUser> sysUsersPerm, SysDept dept) {


        // 删除相关部门id
        List<String> list = new ArrayList<>();
        list.add(dept.getDeptId().toString() + ";");
        // 1、删除本身及子级，若父级仅有一子级则也删除父级
        // 所有子级
        List<SysDept> children = deptMapper.selectChildrenDeptById(dept.getDeptId());
        children.stream().forEach(c -> list.add(c.getDeptId().toString() + ";"));
        List<SysDept> childrenOfParent = deptMapper.selectChildrenDeptById(dept.getParentId());
        if (childrenOfParent.isEmpty()) {
            list.add(dept.getDeptId().toString() + ";");
        }
        String join = String.join("|", list);
        for (SysUser sysUser : sysUsersAudit) {
            String audit = sysUser.getOtherDeptId() == null ? "" : sysUser.getOtherDeptId();
            log.info("审批权限修改前：" + audit);
            audit = audit.replaceAll(join, "");

            log.info("审批权限修改后：" + audit);
            sysUser.setOtherDeptId(audit + dept.getDeptId().toString() + ";");
            userMapper.updateUserAudit(sysUser);
        }
        for (SysUser sysUser : sysUsersPerm) {
            String perm = sysUser.getUserPermission() == null ? "" : sysUser.getUserPermission();
            log.info("数据权限修改前：" + perm);

            perm = perm.replaceAll(join, "");
            log.info("数据权限修改后：" + perm);
            sysUser.setUserPermission(perm + dept.getDeptId().toString() + ";");
            userMapper.updateUserAudit(sysUser);
        }
    }

//    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        list.add("22,");
//        list.add("282,");
//        list.add("262,");
//        list.add("622,");
//        list.add("22,");
//        List<String> list2 = new ArrayList<>();
//        list2.add("282,");
//        list2.add("262,");
//        list2.add("622,");
//        String join = String.join("", list);
//        String join2 = String.join("|", list2);
//        System.out.println(join);
//        String s = join.replaceAll(join2, "");
//        System.out.println(s);
//        String dateStr1 = "2017-03-01 12:33:23";
//        Date date1 = DateUtil.parse(dateStr1);
//
//        String dateStr2 = "2017-03-01 23:33:23";
//        Date date2 = DateUtil.parse(dateStr2);
//
//        //相差一个月，31天
//        long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);
//        System.out.println(betweenDay);

    //    }
    private String addSymbol(String str) {
        if (StrUtil.isNotBlank(str)) {
            String substring = str.substring(str.length() - 1, str.length());
            if (!";".equals(substring)) {
                str = str + ";";
            }
        }
        return str;

    }

    // 插入新数据
    private void updatePerm(SysDept dept) {
        // 新增部门，更新权限，给有其祖级权限的用户授权 sys_user_auth

        String ancestors = dept.getAncestors();
        if (StrUtil.isNotBlank(ancestors)) {
            List<String> strings = Arrays.asList(ancestors.split(","));
            // insert into sys_user_auth (user_id, role_id, dept_id)  select user_id,role_id from sys_user_auth where dept_id in (136,137) group by user_id, role_id
            userMapper.addAuth("insert into sys_user_auth (user_id, role_id, dept_id)  select user_id,role_id," + dept.getDeptId()
                    + " from sys_user_auth where dept_id in (" + String.join(",", strings) + ") group by user_id, role_id");
        }

        // 新增部门则将部门id添加到有其祖级列表权限的用户的审批权限和数据权限
        if ("0".equals(dept.getParentId()) || dept.getParentId() == 0) {
            return;
        }
        // 审批
        List<SysUser> sysUsersAudit = userMapper.selectAudit("%" + dept.getParentId() + ";%");

        for (SysUser sysUser : sysUsersAudit) {
            String audit = sysUser.getOtherDeptId() == null ? "" : sysUser.getOtherDeptId();
            if (!audit.contains(dept.getDeptId().toString())) {
                audit = addSymbol(audit);
                sysUser.setOtherDeptId(audit + dept.getDeptId().toString() + ";");
                userMapper.updateUserAudit(sysUser);
            }
        }
        // 更新角色审批权限
        List<SysRole> sysRoles = userMapper.selectAuditRole("%" + dept.getParentId() + ";%");
        for (SysRole sysRole : sysRoles) {
            String roleAudit = StrUtil.isNullOrUndefined(sysRole.getRoleDept()) || StrUtil.isBlank(sysRole.getRoleDept()) ? "" : sysRole.getRoleDept();
            if (!roleAudit.contains(dept.getDeptId().toString())) {
                roleAudit = addSymbol(roleAudit) + dept.getDeptId().toString() + ";";
                userMapper.updateRoleDept(sysRole.getRoleId().intValue(), roleAudit);
            }
        }
        // 数据
        List<SysUser> sysUsersPerm = userMapper.selectPerm("%" + dept.getParentId() + ";%");
        for (SysUser sysUser : sysUsersPerm) {
            String perm = sysUser.getUserPermission() == null ? "" : sysUser.getUserPermission();
            if (!perm.contains(dept.getDeptId().toString())) {
                perm = addSymbol(perm);
                sysUser.setUserPermission(perm + dept.getDeptId().toString() + ";");
                userMapper.updateUserAudit(sysUser);
            }
        }
    }


    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @RedisEvict(key = "dept_tree_all", fieldKey = "1")
    @RedisEvict2(key = "dept_enabled2", fieldKey = "1")
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {

        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = selectDeptById(dept.getDeptId());

        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + newParentDept.getDeptId() + ",";
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateByPrimaryKeySelective(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatus(dept);
        }
        // 调整部门，更新权限，若父级部门变更则删除原来权限，给有新父级部门权限的用户新增权限
        if (!oldDept.getParentId().equals(dept.getParentId())) {
            // 1、获取本部门及子部门id集合
            List<String> deptIdAndChildrenId = userMapper.getDeptIdAndChildrenId("select dept_id from sys_dept where ancestors like '%," + dept.getDeptId() + ",%'");
            deptIdAndChildrenId.add(dept.getDeptId().toString());
            // 2、删除原权限（本部门及其子部门）
            String join = String.join(",", deptIdAndChildrenId);
            int i = userMapper.deleteAuth("delete from sys_user_auth where dept_id in (" + join + ")");
            // 3、增加新权限（本部门及其子部门）
            String ancestors = dept.getAncestors();
            if (StrUtil.isNotBlank(ancestors)) {
                List<String> strings = Arrays.asList(ancestors.split(","));
                // insert into sys_user_auth (user_id, role_id, dept_id)  select user_id,role_id from sys_user_auth where dept_id in (136,137) group by user_id, role_id
                for (String s : deptIdAndChildrenId) {
                    userMapper.addAuth("insert into sys_user_auth (user_id, role_id, dept_id)  select user_id,role_id," + s
                            + " from sys_user_auth where dept_id in (" + String.join(",", strings) + ") group by user_id, role_id");
                }
            }
        }
        return result;

    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(SysDept dept) {
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        String ancestors = dept.getAncestors();
        dept.setAncestors(ancestors.substring(0, ancestors.length() - 1));
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }

    @Override
    public Set<String> roleDeptIds(Long roleId) {
        return deptMapper.selectRoleDeptIds(roleId);
    }

    /**
     * 查询部门
     *
     * @param roleIds 角色数组
     * @return
     */
    @Override
    public List<SysDept> selectDeptAudit(List<Long> roleIds) {
        return null;
    }

    /**
     * 查询部门所在公司
     *
     * @param deptId
     * @return
     */
    @Override
    public synchronized Map<String, Object> getBelongCompany(Long deptId) {
        List<SysDept> sysDepts = deptMapper.selectDeptList(new SysDept());
//        SysDept company = extracted(deptId);
        SysDept sysDept = new SysDept();
        for (SysDept dept : sysDepts) {
            if (dept.getDeptId().equals(deptId)) {
                sysDept = dept;
            }
        }

        getCompany(sysDepts, sysDept);
        Map<String, Object> cacheMap = redisService.getCacheMap(SystemUtil.getUserId() + "belongCompany");
        redisService.deleteObject(SystemUtil.getUserId() + "belongCompany");
        return cacheMap;
    }

    /**
     * 查询部门所属一级部门
     *
     * @param deptId
     * @return
     */
    @Override
    public synchronized Map<String, Object> getFirstDept(Long deptId) {
        List<SysDept> sysDepts = deptMapper.selectDeptList(new SysDept());
        SysDept sysDept = new SysDept();
        for (SysDept dept : sysDepts) {
            if (dept.getDeptId().equals(deptId)) {
                sysDept = dept;
            }
        }
        if (sysDept.getIsCompany() == 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", sysDept.getDeptName());
            map.put("deptId", sysDept.getDeptId());
            map.put("leaderId", sysDept.getLeaderId());
            return map;
        }
        getFirstDept(sysDepts, sysDept, new SysDept());
        Map<String, Object> cacheMap = redisService.getCacheMap(SystemUtil.getUserId() + "FirstDept");
        redisService.deleteObject(SystemUtil.getUserId() + "FirstDept");
        return cacheMap;
    }

    /**
     * 查询部门所在公司加部门全称
     *
     * @param deptId
     * @return
     */
    @Override
    public String getDeptNameAll(Long deptId) {
        List<SysDept> sysDepts = deptMapper.selectDeptList(new SysDept());
        SysDept sysDept = new SysDept();
        for (SysDept dept : sysDepts) {
            if (dept.getDeptId().equals(deptId)) {
                sysDept = dept;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sysDept.getDeptName());
        getAllName(sysDepts, stringBuilder, sysDept);
        return stringBuilder.toString();
    }

    private void getFirstDept(List<SysDept> sysDepts, SysDept sysDept, SysDept sysDept2) {

        for (SysDept dept : sysDepts) {

            if (dept.getDeptId().equals(sysDept.getParentId())) {
                sysDept2 = sysDept;
                sysDept = dept;
                if (0 == sysDept.getIsCompany()) {
                    getFirstDept(sysDepts, sysDept, sysDept2);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deptName", sysDept2.getDeptName());
                    map.put("deptId", sysDept2.getDeptId());
                    map.put("leaderId", sysDept2.getLeaderId());
                    redisService.setCacheMap(SystemUtil.getUserId() + "FirstDept", map);
                }

            }
        }
    }

    private void getCompany(List<SysDept> sysDepts, SysDept sysDept) {
        System.out.println("测试" + sysDept.getDeptId());
        if (sysDept.getIsCompany() == 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", sysDept.getDeptName());
            map.put("companyId", sysDept.getDeptId());
            redisService.setCacheMap(SystemUtil.getUserId() + "belongCompany", map);
            return;
        }
        for (SysDept dept : sysDepts) {

            if (dept.getDeptId().equals(sysDept.getParentId())) {
                sysDept = dept;
                if (0 == sysDept.getIsCompany()) {
                    getCompany(sysDepts, sysDept);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("companyName", sysDept.getDeptName());
                    map.put("companyId", sysDept.getDeptId());
                    redisService.setCacheMap(SystemUtil.getUserId() + "belongCompany", map);
                }

            }
        }
    }

    /**
     * 查询角色自定义权限部门信息
     *
     * @return
     */
    @Override
    public List<SysDept> listRole(List<String> list) {
        List<SysDept> result = new ArrayList<>();
        for (String deptId : list) {
            if (StrUtil.isBlank(deptId)) {
                continue;
            }
            SysDept sysDept = selectDeptById(Long.valueOf(deptId));
            result.add(sysDept);
        }
        return result;
    }

    /**
     * pc端抄送专用
     *
     * @param sysDept
     * @return
     */
    @Override
    public List<Map<String, Object>> selectDeptList3(SysDept sysDept) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<SysDept> sysDepts = deptMapper.selectDeptList(sysDept);
//        List<SysDept> sysDepts2 = new ArrayList<>();
//
//        for (SysDept dept : sysDepts) {
//            boolean flag = false;
//            // 不是最小部门跳过
//            for (SysDept sysDept1 : sysDepts) {
//                if (dept.getDeptId().equals(sysDept1.getParentId())) {
//                    flag = true;
//                    break;
//                }
//            }
//            if (!flag) {
//                sysDepts2.add(dept);
//            }
//        }
        SysUser sysUser = new SysUser();
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");
        sysUser.setUserName(sysDept.getUserName());
        List<SysUser> sysUsers = userMapper.selectUserList(sysUser);

        for (SysDept dept : sysDepts) {
            StringBuilder str = new StringBuilder(dept.getDeptName());
            getAllName(sysDepts, str, dept);
            sysUsers.stream().forEach(user -> {
                if (user.getDeptId().equals(dept.getDeptId())) {
                    resultList.add(new HashMap<String, Object>() {{
                        put("userId", user.getUserId());
                        put("userName", user.getUserName());
                        put("deptName", str);
                        put("companyName", "");
                    }});
                }
            });
//            Map<String, Object> belongCompany = getBelongCompany(dept.getDeptId());
//            Set<Long> longs = userMapper.selectUserIdsInDepts(new Long[]{dept.getDeptId()});
//            for (Long aLong : longs) {
//                SysUser sysUser = userMapper.selectUserById(aLong);
//                if("1".equals(sysUser.getStatus())){
//                    continue;
//                }
//                resultList.add(new HashMap<String, Object>() {{
////                    put("companyName", belongCompany.get("companyName").toString());
//                    put("companyName","");
//                    put("userId", sysUser.getUserId());
//                    put("userName", sysUser.getUserName());
////                    put("deptName", dept.getDeptName());
//                    put("deptName", str);
//                }});
//            }
        }
        return resultList;
    }

    private void getAllName(List<SysDept> sysDepts, StringBuilder str, SysDept dept) {

        sysDepts.stream().forEach(d -> {
            if (d.getDeptId().equals(dept.getParentId())) {
                str.insert(0, d.getDeptName() + "-");
                if (0 == d.getIsCompany()) {
                    getAllName(sysDepts, str, d);
                }
                return;
            }
        });
    }


    private SysDept extracted(Long deptId) {
        SysDept sysDept = selectDeptById(deptId);
        if (sysDept.getIsCompany() == 0) {
            sysDept = extracted(sysDept.getParentId());
        }
        return sysDept;
    }

    /**
     * 获取树形结构(小程序)
     */
    @Override
    @RedisCache(key = "dept_tree_all", fieldKey = "1")
    public Map<Integer, Object> getTreeAll() {
        return getIntegerObjectMap();

    }

    /**
     * 获取树形结构(有权限)
     *
     * @return
     */
    @Override
    @DataScope(deptAlias = "d")
    public Map<Integer, Object> getTreeAllRole(SysDept dept) {
        return getIntegerObjectMap();
    }

    /**
     * 获取公司列表
     *
     * @return 集合
     */
    @Override
    public List<SysDept> selectCompanyList(SysDept sysDept) {
        return deptMapper.selectCompanyList(sysDept);
    }

    private Map<Integer, Object> getIntegerObjectMap() {
        Map<Integer, Object> integerObjectMap = new HashMap<>();
        List<SysDept> list = deptMapper.selectDeptList(new SysDept());
        List<DeptTreeVo> treeList = new ArrayList<>();
        for (SysDept sysDept : list) {
            treeList.add(new DeptTreeVo(sysDept.getDeptId(), sysDept.getDeptName(), new ArrayList<>(), sysDept.getDeptId(), sysDept.getParentId()));
        }
        DeptTree deptTree = new DeptTree(treeList);
        treeList = deptTree.buildTree();
        Map<Integer, Object> map = new HashMap<>();
        map.put(0, treeList);
        Map<Integer, Object> newTree = getNewTree(treeList, map, 0);
        integerObjectMap = changMap(newTree, treeList.get(0), 1, newTree.size());
        return integerObjectMap;
    }

    private Map<Integer, Object> changMap(Map<Integer, Object> newTree, DeptTreeVo deptTree, int level, int size) {
        if (deptTree.getChildren().isEmpty()) {
            do {
                newTree.put(level, new ArrayList<>());
                level++;
            } while (level < size);
        } else {
            newTree.put(level, deptTree.getChildren());
            changMap(newTree, deptTree.getChildren().get(0), ++level, size);
        }
        return newTree;
    }

    private Map<Integer, Object> getNewTree(List<DeptTreeVo> list, Map<Integer, Object> map, int level) {
        List<DeptTreeVo> list2 = new ArrayList<>();
        boolean flag = false;
        for (DeptTreeVo deptTreeVo : list) {
            if (!deptTreeVo.getChildren().isEmpty()) {
                list2.addAll(deptTreeVo.getChildren());
            }
        }
        map.put(++level, list2);
        for (DeptTreeVo deptTreeVo : list2) {
            if (!deptTreeVo.getChildren().isEmpty()) {
                flag = true;
                break;
            }
        }
        if (flag) {
            getNewTree(list2, map, level);
        }
        return map;
    }

}
