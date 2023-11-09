package com.ruoyi.system.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Districts;
import com.ruoyi.system.service.IDistrictsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 地区管理
 *
 * @author ruoyi
 * @menu 地区管理
 * @date 2018-12-19
 */
@RestController
@RequestMapping("/districts")
public class SysDistrictsController extends BaseController {
    @Autowired
    private IDistrictsService districtsService;

    /**
     * 查询地区列表
     */
    @HasPermissions("system:districts:list")
    @RequestMapping("/list")
    public R list(Districts districts) {
        startPage();
        return result(districtsService.selectDistrictsList(districts));
    }

    /**
     * 查询地区列表(无权限)
     */
    @RequestMapping("/list_enable")
    public R listEnable(Districts districts) {
        return result(districtsService.selectDistrictsList(districts));
    }

    /**
     * 地区树状结构
     */
    @GetMapping("/tree")
    public R getTree() {
        try {
            return R.data(districtsService.getTree());
        } catch (Exception e) {
            logger.error("地区树状结构", e);
            return R.error("地区树状结构获取失败");
        }
    }

    /**
     * 导出地区列表
     */
    @HasPermissions("system:districts:export")
    @GetMapping("/export")
    public void export(HttpServletResponse response, Districts districts) throws IOException {
        List<Districts> list = districtsService.selectDistrictsList(districts);
        ExcelUtil<Districts> util = new ExcelUtil<Districts>(Districts.class);
        util.exportExcel(response, list, "districts");
    }

    /**
     * 新增保存地区
     */
    @HasPermissions("system:districts:add")
    @OperLog(title = "地区", businessType = BusinessType.INSERT)
    @PostMapping("save")
    public R addSave(@RequestBody Districts districts) {
        districts.setPid(districts.getId() / 100);
        districts.setCreateTime(new Date());
        districts.setUpdateTime(new Date());
        districts.setOperator(getLoginName());
        return toAjax(districtsService.insertDistricts(districts));
    }

    /**
     * /**
     * 修改保存地区
     */
    @HasPermissions("system:districts:edit")
    @OperLog(title = "地区", businessType = BusinessType.UPDATE)
    @PostMapping("update")
    public R editSave(@RequestBody Districts districts) {
        districts.setPid(districts.getId() / 100);
        districts.setOperator(getLoginName());
        districts.setUpdateTime(new Date());
        return toAjax(districtsService.updateDistricts(districts));
    }

    /**
     * 删除地区
     */
    @HasPermissions("system:districts:remove")
    @OperLog(title = "地区", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public R remove(String ids) {
        return toAjax(districtsService.deleteDistrictsByIds(ids));
    }

    /**
     * 新增地区
     */
    @HasPermissions("system:districts:add")
    @OperLog(title = "新增地区", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R add(@RequestBody Districts districts) {
        try {
            if (StrUtil.isBlank(districts.getName())) {
                return R.error("地区名字不能为空");
            }
            if (StrUtil.isBlank(districts.getExtName())) {
                return R.error("地区全称不能为空");
            }
            if (districts.getDeep() == null) {
                return R.error("层级不能为空");
            }
            if (districts.getPid() == null) {
                return R.error("父类id不能为空");
            }
            if (districtsService.add(districts)) {
                return R.ok("添加成功");
            }
            return R.error("添加失败");
        } catch (Exception e) {
            logger.error("添加失败，异常信息：" + e);
            return R.error("添加失败");
        }
    }

    /**
     * 修改地区
     */
    @HasPermissions("system:districts:edit")
    @OperLog(title = "修改地区", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R edit(@RequestBody Districts districts) {
        try {
            if (districts.getId() == null) {
                return R.error("地区id不能为空");
            }
            if (StrUtil.isBlank(districts.getName())) {
                return R.error("地区名字不能为空");
            }
            if (StrUtil.isBlank(districts.getExtName())) {
                return R.error("地区全称不能为空");
            }
            if (districts.getPid() == null) {
                return R.error("父类id不能为空");
            }
            if (districts.getDeep() == null) {
                return R.error("层级不能为空");
            }
            if (districtsService.edit(districts)) {
                return R.ok("编辑成功");
            }
            return R.error("编辑失败");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败," + e.getMessage());
        }
    }

    /**
     * 逻辑删除地区
     */
    @HasPermissions("system:districts:edit")
    @OperLog(title = "删除地区", businessType = BusinessType.UPDATE)
    @DeleteMapping("/{ids}")
    public R delete(@PathVariable Integer[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("地区id不能为空");
            }
            if (districtsService.delete(ids)) {
                return R.ok("删除成功");
            }
            return R.error("删除失败");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败," + e.getMessage());
        }
    }
}