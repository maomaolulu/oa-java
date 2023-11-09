package com.ruoyi.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;
import com.ruoyi.activiti.service.BizCarSubsidyApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 还车补贴申请
 *
 * @author zh
 * @date 2022-02-24
 * @menu 还车补贴申请
 */
@RestController
@RequestMapping("biz_car_subsidy_apply")
public class BizCarSubsidyApplyController extends BaseController {
    private final IBizBusinessService bizBusinessService;
    private final BizCarSubsidyApplyService bizCarSubsidyApplyService;

    @Autowired
    public BizCarSubsidyApplyController(IBizBusinessService bizBusinessService, BizCarSubsidyApplyService bizCarSubsidyApplyService) {
        this.bizBusinessService = bizBusinessService;
        this.bizCarSubsidyApplyService = bizCarSubsidyApplyService;
    }

    /**
     * 详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizCarSubsidyApply bizCarSubsidyApply = bizCarSubsidyApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        return R.ok("ok", bizCarSubsidyApply);
    }

    /**
     * 获取用车申请编号
     *
     * @return
     */
    @GetMapping("code")
    @ApiOperation(value = "获取还车补贴申请编号")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("CB" + today + timestamp);
    }

    /**
     * 查询用车申请数据分页(用车记录)车补记录
     *
     * @return
     */
    @GetMapping("listAllPage")
    @ApiOperation(value = "查询还车补贴申请数据分页")
    public R listAll(BizCarSubsidyApply bizCarSubsidyApply) {
        startPage();
        List<BizCarSubsidyApply> bizCarSubsidyApplys = bizCarSubsidyApplyService.listAllPage(bizCarSubsidyApply);
        return result(bizCarSubsidyApplys);
    }

    /**
     * 还车补贴申请新增
     *
     * @param bizCarSubsidyApply
     * @return
     */
    @PostMapping("save")
    @ApiOperation(value = "还车补贴申请新增")
    @OperLog(title = "还车补贴申请新增", businessType = BusinessType.INSERT)
    public R save(@RequestBody BizCarSubsidyApply bizCarSubsidyApply) {
        try {
            List<Map<String, Object>> associateApply = bizCarSubsidyApply.getAssociateApply();
            if (CollUtil.isEmpty(associateApply)) {
                return R.error("请关联用车申请");
            }
            if (associateApply.get(0).get("applyCode") == null) {
                return R.error("请关联用车申请");
            }
            return R.ok("新增成功", bizCarSubsidyApplyService.save(bizCarSubsidyApply));
        } catch (Exception e) {
            logger.error("还车补贴失败", e);
            return R.error("新增失败");
        }

    }

    /**
     * 还车补贴申请修改
     *
     * @param bizCarSubsidyApply
     * @return
     */
    @PostMapping("update")
    @ApiOperation(value = "还车补贴申请修改")
    public R update(@RequestBody BizCarSubsidyApply bizCarSubsidyApply) {
        try {
            return R.ok("修改成功", bizCarSubsidyApplyService.update(bizCarSubsidyApply));
        } catch (Exception e) {
            return R.error("修改失败");
        }

    }

    @PostMapping("delete")
    @ApiOperation(value = "删除还车补贴申请")
    public R delete(@RequestBody Long[] ids) {
        try {
            bizCarSubsidyApplyService.delete(ids);
            return R.ok("删除成功");
        } catch (Exception e) {
            return R.error("删除失败");
        }
    }

    /**
     * 导出
     *
     * @param response
     * @param bizCarSubsidyApply
     * @throws IOException
     */
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, BizCarSubsidyApply bizCarSubsidyApply) throws IOException {
        List<BizCarSubsidyApply> bizCarSubsidyApplys = bizCarSubsidyApplyService.listAllPage(bizCarSubsidyApply);

        bizCarSubsidyApplyService.download(bizCarSubsidyApplys, response);
    }

    /**
     * 导出特定格式的数据
     *
     * @param response
     * @param bizCarSubsidyApply
     * @throws IOException
     */
    @ApiOperation("导出特定格式的数据")
    @GetMapping(value = "/export")
    public void export(HttpServletResponse response, BizCarSubsidyApply bizCarSubsidyApply) throws IOException {
        bizCarSubsidyApplyService.export(bizCarSubsidyApplyService.listAllPage(bizCarSubsidyApply), response);
    }


}
