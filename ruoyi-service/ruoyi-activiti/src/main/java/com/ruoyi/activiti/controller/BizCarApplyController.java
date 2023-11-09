package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.car.BizCarApply;
import com.ruoyi.activiti.domain.dto.CancelUseCarDTO;
import com.ruoyi.activiti.domain.dto.CheckDelayCarDTO;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizCarApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.FileUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 用车申请
 *
 * @author zh
 * @date 2022-02-22
 * @menu 用车申请
 */
@RestController
@RequestMapping("biz_car_apply")
public class BizCarApplyController extends BaseController {
    private final IBizBusinessService bizBusinessService;
    private final BizCarApplyService bizCarApplyService;

    @Autowired
    public BizCarApplyController(IBizBusinessService bizBusinessService, BizCarApplyService bizCarApplyService) {
        this.bizBusinessService = bizBusinessService;
        this.bizCarApplyService = bizCarApplyService;
    }

    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizCarApply bizCarApply = bizCarApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        return R.ok("ok", bizCarApply);
    }

    /**
     * 获取用车申请编号
     *
     * @return
     */
    @GetMapping("code")
    @ApiOperation(value = "获取用车申请编号")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("YC" + today + timestamp);
    }

    /**
     * 查询用车申请数据分页
     *
     * @return
     */
    @GetMapping("listAllPage")
    @ApiOperation(value = "查询用车申请数据分页")
    public R listAll(BizCarApply bizCarApply) {
        startPage();
        List<BizCarApply> bizCarApplys = bizCarApplyService.listAllPage(bizCarApply);
        return result(bizCarApplys);
    }

    @OperLog(title = "导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody BizCarApply bizCarApply) throws IOException {
        bizCarApply.setExport("导出");
        List<BizCarApply> bizCarApplys = bizCarApplyService.listAllPage(bizCarApply);
        ExcelUtil<BizCarApply> util = new ExcelUtil<>(BizCarApply.class);
        String fileName = FileUtil.getDateTimeFormat(2);
        util.exportExcelWithFilename(response, bizCarApplys, "用车申请", fileName);
    }

    @PostMapping("save")
    @ApiOperation(value = "用车申请新增")
    @OperLog(title = "用车申请新增", businessType = BusinessType.INSERT)
    public R save(@RequestBody BizCarApply bizCarApply) {
        try {
            return R.ok("新增成功", bizCarApplyService.save(bizCarApply));
        } catch (Exception e) {
            return R.error("新增失败");
        }

    }

    @PostMapping("update")
    @ApiOperation(value = "用车申请修改")
    public R update(@RequestBody BizCarApply bizCarApply) {
        try {
            return R.ok("修改成功", bizCarApplyService.update(bizCarApply));
        } catch (Exception e) {
            return R.error("修改失败");
        }

    }

    @PostMapping("delete")
    @ApiOperation(value = "删除用车申请")
    public R delete(@RequestBody Long[] ids) {
        try {
            bizCarApplyService.delete(ids);
            return R.ok("删除成功");
        } catch (Exception e) {
            return R.error("删除失败");
        }
    }


    /**
     * 待还车辆数据分页
     *
     * @return
     */
    @GetMapping("getUnpaidCarList")
    @ApiOperation(value = "查询未还车数据分页")
    public R getUnpaidCarList(BizCarApply bizCarApply) {
        try {
            startPage();
            List<BizCarApply> bizCarApplys = bizCarApplyService.getUnpaidCarList(bizCarApply);
            return result(bizCarApplys);
        } catch (Exception e) {
            logger.error("查询未还车数据分页", e);
            return R.error("查询失败");
        }

    }


    /**
     * 取消用车，提前还车,延迟还车
     *
     * @return
     */
    @GetMapping("cancelUseCar")
    @ApiOperation(value = "取消用车，提前还车,延迟还车")
    public R cancelUseCar(CancelUseCarDTO cancelUseCarDTO) {
        try {
            return bizCarApplyService.cancelUseCar(cancelUseCarDTO);
        } catch (Exception e) {
            logger.error("取消用车，提前还车,延迟还车", e);
            return R.error("操作失败");
        }

    }


    /**
     * 延迟还车审核页面
     *
     * @return
     */
    @GetMapping("getCarCheckList")
    @ApiOperation(value = "延迟还车审核页面分页")
    public R getCarCheckList(BizCarApply bizCarApply) {
        try {
            startPage();
            List<BizCarApply> bizCarApplys = bizCarApplyService.getCarCheckList(bizCarApply);
            return result(bizCarApplys);
        } catch (Exception e) {
            logger.error("延迟还车审核页面分页", e);
            return R.error("延迟还车审核页面分页查询失败");
        }

    }


    /**
     * 审核
     *
     * @return
     */
    @GetMapping("check")
    @ApiOperation(value = "审核")
    public R check(CheckDelayCarDTO checkDelayCarDTO) {
        try {
            return bizCarApplyService.checkCarDelay(checkDelayCarDTO);
        } catch (Exception e) {
            logger.error("审核", e);
            return R.error("审核失败");
        }


    }


}