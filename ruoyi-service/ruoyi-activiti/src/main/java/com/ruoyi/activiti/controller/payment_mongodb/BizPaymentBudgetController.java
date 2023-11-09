package com.ruoyi.activiti.controller.payment_mongodb;

import com.ruoyi.activiti.service.BizPaymentBudgetService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * 预测
 *
 * @author wuYang
 * @date 2022/12/9 10:17
 */
@Api(tags = {"预测"})
@RestController
@RequestMapping("/budget")
public class BizPaymentBudgetController extends BaseController {
    @Resource
    BizPaymentBudgetService bizPaymentBudgetService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R excelFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("budgetId") String id, @RequestParam("flag") boolean flag) {
        int upload = bizPaymentBudgetService.upload(file, id, flag);
        if (upload == 500) {
            logger.error("");
            return R.error("版本已改动，请重新下载最新文件");
        }
        if (upload == 400) {
            logger.error("");
            return R.error("名字错误，请重新下载最新文件");
        }
        if (upload == 200) {
            return R.ok("上传成功");
        }
        return R.ok();
    }

    @ApiOperation("文件下载模板")
    @OperLog(title = "文件下载模板", businessType = BusinessType.EXPORT)
    @GetMapping("/download")
    public void excelFileUpload(@RequestParam("id") String id, @RequestParam("flag") Boolean flag, HttpServletResponse response) {

        bizPaymentBudgetService.download(id, flag, response);


    }

    @ApiOperation("文件统计模板")
    @OperLog(title = "文件统计模板", businessType = BusinessType.EXPORT)
    @GetMapping("/out")
    public void out(@RequestParam("id") String id, @RequestParam("flag") Boolean flag, HttpServletResponse response) {

        bizPaymentBudgetService.out(id, flag, response);


    }

    @ApiOperation("预算模板插入保存")
    @OperLog(title = "预算模板插入保存", businessType = BusinessType.INSERT)
    @PostMapping("/template")
    public R budgetInsert(@RequestBody HashMap hashMap) {
        bizPaymentBudgetService.budgetInsert(hashMap);
        return R.ok("保存成功");
    }

    @ApiOperation("预算模板发布")
    @OperLog(title = "预算模板发布", businessType = BusinessType.INSERT)
    @GetMapping("/state")
    public R budgetPublish(@RequestParam("id") String id) {
        bizPaymentBudgetService.budgetPublish(id);
        return R.ok("发布成功");
    }

    @ApiOperation("预算模板更新")
    @OperLog(title = "预算模板更新", businessType = BusinessType.UPDATE)
    @PutMapping("/template")
    public R budgetUpdate(@RequestBody HashMap hashMap) {
        bizPaymentBudgetService.budgetUpdate(hashMap);
        return R.ok();
    }

    @ApiOperation("查询预算模板")
    @GetMapping("/template")
    public R budgetSelect(@RequestParam("id") String id) {
        try {
            bizPaymentBudgetService.budgetSelect(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }

    @ApiOperation("前端传数组查部门预算")
    @PostMapping("/select/dept")
    public R budgetSelectByDept(@RequestBody HashMap map) {
        try {
            List<HashMap> hashMaps = bizPaymentBudgetService.handelDeptIdToObject(map);
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }

    @ApiOperation("前端传数组查明细预算")
    @PostMapping("/select/detail")
    public R budgetSelectByDetail(@RequestBody HashMap map) {
        try {
            List<HashMap> hashMaps = bizPaymentBudgetService.handeDetailIdToObject(map);
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }

    @ApiOperation("查询公司预算计划(规定部门有机会公司必须有)")
    @GetMapping("/select/plan")
    public R selectPlanALL(@RequestParam("id") String id) {
        try {
            List<HashMap> hashMaps = bizPaymentBudgetService.selectForPlan(id);
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }

    @ApiOperation("查看公司预算计划(规定部门有机会公司必须有)")
    @GetMapping("/select/look")
    public R selectLookALL(@RequestParam("id") String id) {
        try {
            List<HashMap> hashMaps = bizPaymentBudgetService.selectForLook(id);
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }

    @ApiOperation("查看当月预算")
    @PostMapping("/select/money")
    public R selectMoney(@RequestBody HashMap map) {
        try {
            String hashMaps = bizPaymentBudgetService.selectMoney(map);
            if (hashMaps == null) {
                return R.error("查不到该付款类型再该月的预算");
            }
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("查看当月预算", e);
            return R.error("查看当月预算");
        }
    }

    @ApiOperation("手动调剂成功")
    @PostMapping("/reset/money")
    public R resetMoney(@RequestBody HashMap map) {
        try {
            int i = bizPaymentBudgetService.resetMoney(map);
            if (i == 0) {
                return R.error("手动调剂失败 提交数据有问题");
            }
            return R.ok("手动调剂成功");
        } catch (Exception e) {
            logger.error("手动调剂失败", e);
            return R.error("手动调剂失败");
        }
    }


    @ApiOperation("删除数据")
    @OperLog(title = "删除数据", businessType = BusinessType.DELETE)
    @PostMapping("/delete/array")
    public R deleteArray(@RequestBody HashMap param) {
        try {
            List<String> ids = (List<String>) param.get("ids");
            ids.remove("6360d740a6304d208a4dbd09");
            String id = (String) param.get("id");
            ids.remove(id);
            bizPaymentBudgetService.delete(ids, id);
            return R.data(ids);
        } catch (Exception e) {
            logger.error("查询预算模板失败", e);
            return R.error("查询预算模板失败");
        }
    }


    @ApiOperation("更新模板名字")
    @GetMapping("/update/name")
    @OperLog(title = "更新模板名字", businessType = BusinessType.UPDATE)
    public R selectPlanALL(@RequestParam("id") String id, @RequestParam("name") String name) {
        try {
            bizPaymentBudgetService.updateName(id, name);
            return R.ok("更新模板名字成功");
        } catch (Exception e) {
            logger.error("更新模板名字失败", e);
            return R.error("更新模板名字失败");
        }
    }

    @ApiOperation("预算编辑后保存")
    @OperLog(title = "预算编辑后保存", businessType = BusinessType.UPDATE)
    @PostMapping("/save/dept")
    public R budgetSaveByDept(@RequestBody HashMap map) {
        try {
            int i = bizPaymentBudgetService.savePlan(map);
            return R.ok("保存成功");
        } catch (Exception e) {
            logger.error("预算模板保存失败", e);
            return R.error("预算模板保存失败");
        }
    }


    @ApiOperation("查询预算模板所有")
    @GetMapping("/templateAll")
    public R budgetSelectAll(@RequestParam(value = "budgetName", required = false) String budgetName,
                             @RequestParam(value = "state", required = false) Integer state) {
        try {
            startPage();
            List<HashMap> hashMaps = bizPaymentBudgetService.budgetSelectAll(budgetName, state);
            return resultData(hashMaps);
        } catch (Exception e) {
            logger.error("message", e);
            return R.error();
        }
    }

    @ApiOperation("禁用")
    @OperLog(title = "禁用", businessType = BusinessType.UPDATE)
    @GetMapping("/templateDisable")
    public R budgetDisable(String id) {
        int i = bizPaymentBudgetService.budgetDisable(id);
        if (i == 1) {
            return R.ok("启用完成");
        } else {
            return R.ok("禁用完成");
        }

    }

    @ApiOperation("删除")
    @OperLog(title = "删除", businessType = BusinessType.UPDATE)
    @GetMapping("/templateDelete")
    public R budgetDelete(String id) {
        bizPaymentBudgetService.budgetDelete(id);
        return R.ok("删除完成");
    }

    @ApiOperation("更新已发布预算")
    @OperLog(title = "更新已发布预算", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBudgetMoney")
    public R updateBudgetMoney(@RequestBody HashMap hashMap) {
        bizPaymentBudgetService.updateBudgetMoney(hashMap);
        return R.ok();
    }

}
