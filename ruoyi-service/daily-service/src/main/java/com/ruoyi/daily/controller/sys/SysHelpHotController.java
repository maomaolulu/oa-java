package com.ruoyi.daily.controller.sys;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.sys.SysHelpHot;
import com.ruoyi.daily.domain.sys.SysHelpVersion;
import com.ruoyi.daily.domain.sys.dto.SysHelpHotDTO;
import com.ruoyi.daily.domain.sys.dto.SysHelpVersionDTO;
import com.ruoyi.daily.domain.sys.dto.group.Insert;
import com.ruoyi.daily.domain.sys.dto.group.Update;
import com.ruoyi.daily.service.sys.SysHelpHotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 热点问题管理
 * Created by WuYang on 2022/8/18 15:27
 */
@Api(tags = {"热点问题"})
@RestController
@RequestMapping("daily")
public class SysHelpHotController extends BaseController {
    @Resource
    SysHelpHotService helpHotService;

    /**
     * 新增热点问题
     */
    @ApiOperation("新增热点问题")
    @PostMapping("hot")
    @OperLog(title = "新增热点问题", businessType = BusinessType.INSERT)
    public R add(@RequestBody @Validated(Insert.class) SysHelpHotDTO hotDTO) {
        try {
            helpHotService.add(hotDTO);
            return R.ok();
        } catch (Exception e) {
            logger.error("新增热点问题失败", e);
            return R.error("新增热点问题失败");
        }

    }

    /**
     * 更新热点问题
     */
    @ApiOperation("更新热点问题")
    @PutMapping("hot")
    @OperLog(title = "更新热点问题", businessType = BusinessType.UPDATE)
    public R update(@RequestBody @Validated(Update.class) SysHelpHotDTO hotDTO) {
        try {
            helpHotService.update(hotDTO);
            return R.ok();
        } catch (Exception e) {
            logger.error("更新热点问题失败", e);
            return R.error("更新热点问题失败");
        }
    }

    /**
     * 删除热点问题
     */
    @ApiOperation("删除热点问题")
    @DeleteMapping("hot")
    @OperLog(title = "删除热点问题", businessType = BusinessType.DELETE)
    public R delete(@RequestParam("id") Long id) {
        try {
            helpHotService.delete(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("删除热点问题失败", e);
            return R.error("删除热点问题失败");
        }
    }

    /**
     * 查询所有
     */
    @ApiOperation("查询所有")
    @GetMapping("hot/list")
    public R selectList() {
        try {
            List<SysHelpHot> lists = helpHotService.getLists();
            return R.data(lists);
        } catch (Exception e) {
            logger.error("查询所有热点问题失败", e);
            return R.error("查询所有热点问题失败");
        }
    }

    /**
     * 查询一个热点问题
     */
    @ApiOperation("查询一个热点问题")
    @GetMapping("hot")
    public R select(@RequestParam("id") Long id) {
        try {
            SysHelpHot sysHelpHot = helpHotService.get(id);
            return R.data(sysHelpHot);
        } catch (Exception e) {
            logger.error("查询一个热点问题失败", e);
            return R.error("查询一个热点问题失败");
        }
    }

}
