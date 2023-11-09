package com.ruoyi.daily.controller.sys;

import cn.hutool.core.util.PageUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.sys.SysHelpVersion;
import com.ruoyi.daily.domain.sys.dto.SysHelpVersionDTO;
import com.ruoyi.daily.domain.sys.dto.group.Insert;
import com.ruoyi.daily.domain.sys.dto.group.Update;
import com.ruoyi.daily.service.sys.SysHelpVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 版本公告 Api
 * Created by WuYang on 2022/8/18 11:20
 */
@Api(tags = {"版本服务"})
@RestController
@RequestMapping("daily/help")
public class SysHelpVersionController extends BaseController {

    @Resource
    SysHelpVersionService versionService;

    /**
     * 新增版本
     */
    @ApiOperation("新增版本")
    @PostMapping("version")
    @OperLog(title = "新增版本",businessType = BusinessType.INSERT)
    public R add(@RequestBody @Validated(Insert.class) SysHelpVersionDTO sysHelpVersion) {
        try {
            versionService.add(sysHelpVersion);
            return R.ok("新增版本成功");
        } catch (Exception e) {
            logger.error("新增版本失败",e);
            return R.error("新增版本失败");
        }

    }

    /**
     * 修改版本信息
     */
    @ApiOperation("修改版本信息")
    @PutMapping("version")
    @OperLog(title = "修改版本信息",businessType = BusinessType.UPDATE)
    public R update(@RequestBody @Validated(Update.class) SysHelpVersionDTO sysHelpVersion) {
        try {
            versionService.update(sysHelpVersion);
            return R.ok("修改版本信息成功");
        } catch (Exception e) {
            logger.error("修改版本信息失败",e);
            return R.error("修改版本信息失败");
        }
    }

    /**
     * 删除版本信息
     */
    @ApiOperation("删除版本信息")
    @DeleteMapping("version")
    @OperLog(title = "删除版本信息",businessType = BusinessType.DELETE)
    public R delete(@RequestParam("id") Long id) {
        try {
            versionService.delete(id);
            return R.ok("删除版本信息成功");
        } catch (Exception e) {
            logger.error("删除版本信息失败",e);
            return R.error("删除版本信息失败");
        }
    }

    /**
     * 查询所有
     */
    @ApiOperation("查询所有版本")
    @GetMapping("version/list")
    public R selectList(@RequestParam("type") Integer type) {
        try {
            List<SysHelpVersion> lists = versionService.getLists(type);
            return R.data(lists);
        } catch (Exception e) {
            logger.error("查询所有版本失败",e);
            return R.error("查询所有版本失败");
        }
    }
    /**
     * 查询所有分页
     */
    @ApiOperation("查询所有版本分页")
    @GetMapping("version/pagelist")
    public R selectPageList(@RequestParam("type") Integer type) {
        try {
            pageUtil();
            List<SysHelpVersion> lists = versionService.getLists(type);
            return resultData(lists);
        } catch (Exception e) {
            logger.error("查询所有版本失败",e);
            return R.error("查询所有版本失败");
        }
    }

    /**
     * 查询一个版本
     */
    @ApiOperation("查询一个版本")
    @GetMapping("version")
    public R select(@RequestParam("id")Long id) {
        try {
            SysHelpVersion sysHelpVersion = versionService.get(id);
            return R.data(sysHelpVersion);
        } catch (Exception e) {
            logger.error("查询一个版本失败",e);
            return R.error("查询一个版本失败");
        }
    }

}
