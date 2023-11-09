package com.ruoyi.daily.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.sys.SysUserAuth;
import com.ruoyi.daily.domain.sys.dto.SysUserAuthDto;
import com.ruoyi.daily.mapper.sys.SysUserAuthMapper;
import com.ruoyi.daily.service.sys.SysUserAuthService;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 权限管理
 * @author zx
 * @date 2022-10-21 17:39:27
 */
@Service
public class SysUserAuthServiceImpl implements SysUserAuthService {
    private final SysUserAuthMapper sysUserAuthMapper;
    @Autowired
    public SysUserAuthServiceImpl(SysUserAuthMapper sysUserAuthMapper) {
        this.sysUserAuthMapper = sysUserAuthMapper;
    }

    /**
     * 查询用户的角色
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysRole> getRoleByUserId(Long userId) {
        List<SysRole> roleList = sysUserAuthMapper.getRoleByUserId(userId);
        return roleList;
    }

    /**
     * 获取权限部门
     *
     * @param userId
     * @param roleId
     * @return
     */
    @Override
    public List<Long> getDepts(Long userId, Long roleId) {
        QueryWrapper<SysUserAuth> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("role_id",roleId);
        List<SysUserAuth> sysUserAuths = sysUserAuthMapper.selectList(wrapper);
        List<Long> depts = new ArrayList<>(sysUserAuths.size());
        sysUserAuths.stream().forEach(sysUserAuth -> depts.add(sysUserAuth.getDeptId()));
        return depts;
    }

    /**
     * 保存
     *
     * @param authDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUserAuthDto authDto) {
        Long userId = authDto.getUserId();
        Long roleId = authDto.getRoleId();
        // 删除旧数据
        QueryWrapper<SysUserAuth> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("role_id",roleId);
        sysUserAuthMapper.delete(wrapper);
        // 保存新数据
        List<Long> deptIdList = authDto.getDeptIdList();
        for (Long deptId : deptIdList) {
            SysUserAuth userAuth = new SysUserAuth();
            userAuth.setUserId(userId);
            userAuth.setRoleId(roleId);
            userAuth.setDeptId(deptId);
            userAuth.setCreateTime(new Date());
            userAuth.setCreateBy(SystemUtil.getUserNameCn());
            sysUserAuthMapper.insert(userAuth);
        }
    }
}
