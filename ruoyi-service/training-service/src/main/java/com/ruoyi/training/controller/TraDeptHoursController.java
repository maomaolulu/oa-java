package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraDeptHours;
import com.ruoyi.training.service.TraDeptHoursService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门学时接口集合(培训目标)
 *
 * @author hjy
 * @date 2022/6/15 15:00
 */
@RestController
@RequestMapping("/training/hour")
public class TraDeptHoursController extends BaseController {

    private final TraDeptHoursService hoursService;

    public TraDeptHoursController(TraDeptHoursService hoursService) {
        this.hoursService = hoursService;
    }

    /**
     * 查询部门学时列表
     */
    @GetMapping("/list")
    public R list(TraDeptHours traDeptHours) {
        try {
            pageUtil();
            List<TraDeptHours> list = hoursService.selectTraDeptHoursList(traDeptHours);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询部门学时信息失败，异常信息：" + e);
            return R.error("查询部门学时信息失败.");
        }
    }

    /**
     * 获取部门学时详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("部门学时id不能为空！");
            }
            return R.data(hoursService.selectTraDeptHoursById(id));
        } catch (Exception e) {
            logger.error("获取部门学时信息失败，异常信息：" + e);
            return R.error("获取部门学时信息失败.");
        }
    }

    /**
     * 新增部门学时
     */
    @OperLog(title = "部门学时", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody TraDeptHours traDeptHours) {
        try {
            if (hoursService.insertTraDeptHours(traDeptHours) > 0) {
                return R.ok("部门学时信息新增成功！");
            }
            return R.error("部门学时信息新增失败！");
        } catch (Exception e) {
            logger.error("部门学时信息新增失败，异常信息：" + e);
            return R.error("部门学时信息新增失败.");
        }
    }

    /**
     * 检查该部门是否已经创建过部门学时目标 0:可创建  ； 其他（记录id）：已创建
     */
    @PostMapping("/checked")
    public R checkedDept(String deptId) {
        try {
            TraDeptHours deptHours = hoursService.checkedDept(deptId);
            if (deptHours == null) {
                return R.ok("可创建", 0);
            } else {
                return R.ok("该部门已创建过学时目标", deptHours.getId());
            }
        } catch (Exception e) {
            return R.error("检查部门学时信息失败.");
        }
    }

    /**
     * 修改部门学时
     */
    @OperLog(title = "部门学时", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraDeptHours traDeptHours) {
        try {
            if (hoursService.updateTraDeptHours(traDeptHours) > 0) {
                return R.ok("部门学时信息修改成功！");
            }
            return R.error("部门学时信息修改失败！");
        } catch (Exception e) {
            logger.error("获取部门学时信息失败，异常信息：" + e);
            return R.error("获取部门学时信息失败.");
        }
    }

    /**
     * 删除部门学时
     */
    @OperLog(title = "部门学时", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的部门学时id不能为空！");
            }
            if (hoursService.deleteTraDeptHoursByIds(ids) > 0) {
                return R.ok("部门学时信息删除成功！");
            }
            return R.error("部门学时信息删除失败！");
        } catch (Exception e) {
            logger.error("删除部门学时信息失败，异常信息：" + e);
            return R.error("删除部门学时信息失败.");
        }
    }


}
