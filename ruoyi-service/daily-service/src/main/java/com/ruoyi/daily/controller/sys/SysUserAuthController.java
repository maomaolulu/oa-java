package com.ruoyi.daily.controller.sys;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.domain.sys.dto.SysUserAuthDto;
import com.ruoyi.daily.service.sys.SysUserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 权限管理
 * @author zx
 * @date 2022-10-21 17:39:27
 */
@RestController
@RequestMapping("auth")
public class SysUserAuthController extends BaseController {
    private final SysUserAuthService sysUserAuthService;
    @Autowired
    public SysUserAuthController(SysUserAuthService sysUserAuthService) {
        this.sysUserAuthService = sysUserAuthService;
    }

    /**
     * 查询角色列表
     * @param userId
     * @return
     */
    @GetMapping("roles")
    public R getRoleByUserId(Long userId){
        try {
            return R.data(sysUserAuthService.getRoleByUserId(userId));
        }catch (Exception e){
            logger.error("获取角色失败：{}",e);
            return R.error("获取角色失败");
        }
    }


    /**
     * 查询部门列表
     * @param userId
     * @return
     */
    @GetMapping("depts")
    public R getDepts(Long userId,Long roleId){
        try {
            return R.data(sysUserAuthService.getDepts(userId,roleId));
        }catch (Exception e){
            logger.error("获取权限部门失败：{}",e);
            return R.error("获取权限部门失败");
        }
    }

    /**
     * 保存
     * @param authDto
     * @return
     */
    @PostMapping("info")
    public R save(@RequestBody SysUserAuthDto authDto){
        try {
            sysUserAuthService.save(authDto);
            return R.ok();
        }catch (Exception e){
            logger.error("保存失败：{}",e);
            return R.error("保存失败");
        }
    }





}
