package com.ruoyi.system.feign;

import com.ruoyi.common.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 用户 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    @GetMapping("user/get/{userId}")
    public SysUser selectSysUserByUserId(@PathVariable("userId") long userId);

    @GetMapping("user/find/{username}")
    SysUser selectSysUserByUsername(@PathVariable("username") String username);

    @GetMapping("user/query/{username}")
    SysUser selectSysUserByName(@PathVariable("username") String username);

    @GetMapping("user/queryByEmail/{email}")
    SysUser selectSysUserByEmail(@PathVariable("email") String email);

    @PostMapping("user/update/login")
    public R updateUserLoginRecord(@RequestBody SysUser user);

    @GetMapping("user/getLeaderInfo/{userId}")
    SysUser getLeaderInfo(@PathVariable("userId") Long userId);

    /**
     * 查询拥有当前角色的所有用户
     *
     * @param roleIds
     * @return
     * @author zmr
     */
    @GetMapping("user/hasRoles")
    public Set<Long> selectUserIdsHasRoles(@RequestParam("roleIds") String roleIds);

    /**
     * 查询所有当前部门中的用户
     *
     * @param deptIds
     * @return
     * @author zmr
     */
    @GetMapping("user/inDepts")
    public Set<Long> selectUserIdsInDepts(@RequestParam("deptIds") String deptIds);

    /**
     * 绑定微信
     *
     * @param sysUser
     * @return
     */
    @PostMapping("user/update_wx2")
    public R editSaveWx2(@RequestBody SysUser sysUser, @RequestHeader("current_username") String userStr, @RequestHeader("cn_username") String cnUsername);

}
