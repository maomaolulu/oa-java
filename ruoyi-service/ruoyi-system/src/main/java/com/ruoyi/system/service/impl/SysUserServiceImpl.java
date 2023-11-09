package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private ISysConfigService configService;

    private final SysUserDeptMapper sysUserDeptMapper;
    private final ISysDeptService sysDeptService;

    @Autowired
    public SysUserServiceImpl(SysUserDeptMapper sysUserDeptMapper, ISysDeptService sysDeptService) {
        this.sysUserDeptMapper = sysUserDeptMapper;
        this.sysDeptService = sysDeptService;
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {

        List<SysUser> sysUsers = userMapper.selectUserList(user);
        for (SysUser sysUser : sysUsers) {
            if (StrUtil.isNotEmpty(sysUser.getOtherDeptId())) {
                sysUser.setOtherDeptIds(Arrays.asList(sysUser.getOtherDeptId().split(";")));
            }
            if (StrUtil.isNotEmpty(sysUser.getUserPermission())) {
                sysUser.setUserPermissions(Arrays.asList(sysUser.getUserPermission().split(";")));
            }
        }
        return sysUsers;
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList2(SysUser user) {

        List<SysUser> sysUsers = userMapper.selectUserList(user);
        for (SysUser sysUser : sysUsers) {
            if (StrUtil.isNotEmpty(sysUser.getOtherDeptId())) {
                sysUser.setOtherDeptIds(Arrays.asList(sysUser.getOtherDeptId().split(";")));
            }
        }
        List<SysUser> collect = sysUsers.stream().filter(sysUser -> sysUser.getDelFlag().equals("0") && sysUser.getStatus().equals("0")).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByLoginName(String userName) {
        return userMapper.selectUserByLoginName(userName);
    }

    /**
     * 通过用户名查询用户(中文名)
     *
     * @param userName 用户名(中文名)
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过手机号码查询用户
     *
     * @param phoneNumber 手机号码
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByPhoneNumber(String phoneNumber) {
        return userMapper.selectUserByPhoneNumber(phoneNumber);
    }

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        SysUser sysUser = userMapper.selectUserById(userId);
        if (StrUtil.isNotEmpty(sysUser.getOtherDeptId())) {
            sysUser.setOtherDeptIds(Arrays.asList(sysUser.getOtherDeptId().split(";")));
        }
        if (StrUtil.isNotEmpty(sysUser.getUserPermission())) {
            sysUser.setUserPermissions(Arrays.asList(sysUser.getUserPermission().split(";")));
        }
        if (sysUser.getDept().getParentId() != null) {
            Map<String, Object> belongCompany = sysDeptService.getBelongCompany(sysUser.getDept().getDeptId());
            sysUser.setCompany(belongCompany.get("companyName").toString());
            sysUser.setCompanyId((Long) belongCompany.get("companyId"));

        }
        return sysUser;
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        // 删除用户与部门关联
        sysUserDeptMapper.deleteUserDeptByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUserByIds(String ids) throws BusinessException {
        Long[] userIds = Convert.toLongArray(ids);
        for (Long userId : userIds) {
            if (SysUser.isAdmin(userId)) {
                throw new BusinessException("不允许删除超级管理员用户");
            }
        }
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {

        int rows = userMapper.insertUser(user);
        // 关联优维系统
//        if(user.isBindUniwe()){
//            Map<String,Object> map = new HashMap<>(2);
//            map.put("username",user.getLoginName());
//            map.put("oaId",user.getUserId());
//            String post = HttpUtil.post("http://192.168.0.69:8080/auth/relationOa", map);
//            R r = JSON.parseObject(post, R.class);
//            if((int)r.get("code")!=200){
//                log.error("关联失败",r.get("msg").toString());
//                throw new StatefulException("关联失败");
//            }
//
//        }
        // 新增用户信息
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        // 新增用户与部门关联
        insertUserDept(user);
        return rows;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        user.setUpdateBy(SystemUtil.getUserNameCn());
        Long userId = user.getUserId();
        // 删除用户与部门关联
        sysUserDeptMapper.deleteUserDeptByUserId(userId);
        // 新增用户与部门关联
        insertUserDept(user);
        // 删除用户权限（新）
        deleteUserAuth(user);
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUserAudit(user);
    }

    private void deleteUserAuth(SysUser user) {
        List<Long> roles = user.getRoleIds();
        List<Long> rolesOld = userRoleMapper.selectRolesByUserId(user.getUserId());
        rolesOld.removeAll(roles);
        for (Long role : rolesOld) {
            userRoleMapper.deleteUserAuth(user.getUserId(), role);
        }
    }

    /**
     * 保存登录信息
     *
     * @param user
     * @return
     */
    @Override
    public int updateUser2(SysUser user) {
        return userMapper.updateUser2(user);
    }

    /**
     * 新增用户部门信息
     *
     * @param user 用户对象
     */
    public void insertUserDept(SysUser user) {
        List<String> userPermissions = user.getUserPermissions();
        if (StringUtils.isNotNull(userPermissions)) {
            // 新增用户与部门管理
            List<SysUserDept> list = new ArrayList<>();
            for (String deptId : userPermissions) {
                SysUserDept up = new SysUserDept();
                up.setUserId(user.getUserId());
                up.setDeptId(Long.valueOf(deptId));
                list.add(up);
            }
            if (list.size() > 0) {
                sysUserDeptMapper.batchUserDept(list);
            }
        }
    }

    /**
     * 修改用户个人详细信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserInfo(SysUser user) {
        user.setUpdateTime(new Date());
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetUserPwd(SysUser user) {
        user.setUpdateBy(SystemUtil.getUserNameCn());
        return updateUserInfo(user);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        List<Long> roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                userPostMapper.batchUserPost(list);
            }
        }
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param loginName 用户名
     * @return
     */
    @Override
    public String checkLoginNameUnique(String loginName) {
        int count = userMapper.checkLoginNameUnique(loginName);
        if (count > 0) {
            return UserConstants.USER_NAME_NOT_UNIQUE;
        }
        return UserConstants.USER_NAME_UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.USER_PHONE_NOT_UNIQUE;
        }
        return UserConstants.USER_PHONE_UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.USER_EMAIL_NOT_UNIQUE;
        }
        return UserConstants.USER_EMAIL_UNIQUE;
    }

    /**
     * 查询用户所属角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(Long userId) {
        List<SysRole> list = roleMapper.selectRolesByUserId(userId);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(Long userId) {
        List<SysPost> list = postMapper.selectPostsByUserId(userId);
        StringBuffer idsStr = new StringBuffer();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new BusinessException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByLoginName(user.getLoginName());
                if (StringUtils.isNull(u)) {
                    user.setPassword(Md5Utils.hash(user.getLoginName() + password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getLoginName() + " 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getLoginName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getLoginName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getLoginName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 用户状态修改
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int changeStatus(SysUser user) {
        if (SysUser.isAdmin(user.getUserId())) {
            throw new BusinessException("不允许修改超级管理员用户");
        }
        user.setUpdateBy(SystemUtil.getUserNameCn());
        return userMapper.updateUser(user);
    }


    @Override
    public Set<Long> selectUserIdsHasRoles(Long[] roleIds) {
        return ArrayUtil.isNotEmpty(roleIds) ? userMapper.selectUserIdsHasRoles(roleIds) : null;
    }


    @Override
    public Set<Long> selectUserIdsInDepts(Long[] deptIds) {
        return ArrayUtil.isNotEmpty(deptIds) ? userMapper.selectUserIdsInDepts(deptIds) : null;
    }

    @Override
    public List<SysUser> selectUserIdsInDept(Long deptId) {
        return deptId != null ? userMapper.selectUserIdsInDept("%," + deptId + ",%", deptId.toString()) : new ArrayList<>();
    }

    /**
     * 通过用户信息获取部门主管信息
     *
     * @param userId 用户id
     * @return result
     */
    @Override
    public SysUser selectLeaderByUserId(Long userId) {
        return sysUserDeptMapper.selectLeaderByUserId(userId);
    }

    /**
     * 获取公司下的所有人员
     *
     * @param companyId 公司ID
     * @return 集合
     */
    @Override
    public List<SysUser> selectUsersBelongCompany(Long companyId) {
        SysUser sysUser = new SysUser();
        sysUser.setStatus("0");
        List<SysUser> users = userMapper.selectUserList(sysUser);
        List<SysDept> sysDepts = sysDeptService.selectDeptList(new SysDept());
        Map<Long, SysDept> deptMap = sysDepts.stream().collect(Collectors.toMap(SysDept::getDeptId, dep -> dep));
        List<SysUser> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(users)) {
            for (SysUser user : users) {
                user.setSalt(null);
                user.setPassword(null);
                user.setUsernamePy(Arrays.asList(PinyinUtil.getPinyin(user.getUserName()).split(" ")));
                Long companyKey = getParentId(user.getDeptId(), deptMap);
                if (companyId.equals(companyKey)) {
                    list.add(user);
                }
            }
        }
        return list;
    }

    /**
     * 获取父ID
     *
     * @param deptId  部门ID
     * @param deptMap 部门信息集合
     * @return 父ID
     */
    private Long getParentId(Long deptId, Map<Long, SysDept> deptMap) {
        SysDept dept = deptMap.get(deptId);
        if (dept == null || dept.getDeptId() == null) return 0L;
        if (dept.getParentId() != 0) {
            deptId = getParentId(dept.getParentId(), deptMap);
        }
        return deptId;
    }

    /**
     * 绑定解绑微信\绑定app-cid
     *
     * @param sysUser
     * @return
     */
    @Override
    public R editSaveWx(SysUser sysUser) {
        return R.ok("操作成功", userMapper.updateUser(sysUser));
    }

    /**
     * 修改用户头像
     *
     * @param sysUser
     * @return
     */
    @Override
    public int updateAvatar(SysUser sysUser) {
        SysUser sysUserOld = userMapper.selectUserById(sysUser.getUserId());
//        fileService.update("avatar", sysUserOld.getUserId(), null);
//
//        List<SysAttachment> avatar = fileService.getList(sysUserOld.getUserId(), "avatar");
//        if (avatar.size() > 0) {
//            List<SysAttachment> collect = avatar.stream().sorted(Comparator.comparing(SysAttachment::getId).reversed()).collect(Collectors.toList());
//            String avatarStr = fileService.getFileUrls("avatar",collect.get(0).getUrl());
//            sysUserOld.setAvatar(avatarStr);
//        }
        sysUserOld.setAvatar(sysUser.getAvatar());
        sysUserOld.setUpdateBy(SystemUtil.getUserNameCn());
        return userMapper.updateUser(sysUserOld);
    }
}
