package com.ruoyi.system.controller;

import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

import java.util.List;

/**
 * 参数配置 提供者
 *
 * @author zmr
 * @date 2019-05-20
 * @menu 参数配置
 */
@RestController
@RequestMapping("config")
public class SysConfigController extends BaseController {

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询参数配置
     */
    @GetMapping("get/{configId}")
    public SysConfig get(@PathVariable("configId") Long configId) {
        return sysConfigService.selectConfigById(configId);

    }

    /**
     * 查询参数配置列表
     */
    @GetMapping("list")
    public R list(SysConfig sysConfig) {
        startPage();
        return result(sysConfigService.selectConfigList(sysConfig));
    }

    /**
     * 查询参数配置列表
     */
    @PostMapping("list/operating")
    public List<SysConfig> listOperating(@RequestBody SysConfig sysConfig) {
        return sysConfigService.selectConfigList(sysConfig);
    }


    /**
     * 新增保存参数配置
     */
    @PostMapping("save")
    @OperLog(title = "新增保存参数配置", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysConfig sysConfig) {
        return toAjax(sysConfigService.insertConfig(sysConfig));
    }

    /**
     * 修改保存参数配置
     */
    @HasPermissions("operating:self:update")
    @PostMapping("update")
    @OperLog(title = "修改保存参数配置", businessType = BusinessType.UPDATE)
    public R editSave(@RequestBody SysConfig sysConfig) {
        return toAjax(sysConfigService.updateConfig(sysConfig));
    }

    /**
     * 删除参数配置
     */
    @PostMapping("remove")
    @OperLog(title = "删除参数配置", businessType = BusinessType.DELETE)
    public R remove(String ids) {
        return toAjax(sysConfigService.deleteConfigByIds(ids));
    }

    /**
     * 获取不同环境url
     */
    @GetMapping("/url")
    public SysConfig findConfigUrl() {
        return sysConfigService.findConfigUrl();
    }
}
