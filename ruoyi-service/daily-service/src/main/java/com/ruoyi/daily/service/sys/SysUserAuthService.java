package com.ruoyi.daily.service.sys;

import com.ruoyi.daily.domain.sys.dto.SysUserAuthDto;
import com.ruoyi.system.domain.SysRole;

import java.util.List;
/**
 * 权限管理
 * @author zx
 * @date 2022-10-21 17:39:27
 */
public interface SysUserAuthService {
    /**
     * 查询用户的角色
     * @param userId
     * @return
     */
    List<SysRole> getRoleByUserId(Long userId);

    /**
     * 获取权限部门
     * @param userId
     * @param roleId
     * @return
     */
    List<Long> getDepts(Long userId, Long roleId);

    /**
     * 保存
     * @param authDto
     */
    void save(SysUserAuthDto authDto);
}
