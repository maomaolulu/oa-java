package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.domain.dto.BizProjectAmountApplyDto;
import com.ruoyi.activiti.domain.my_apply.BizProjectAmountApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizProjectAmountApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目金额调整申请
 *
 * @author zh
 * @date 2023/03/23
 */
@Slf4j
@RestController
@RequestMapping("project_amount_apply")
public class BizProjectAmountApplyController extends BaseController {
    private final IBizBusinessService businessService;
    private final BizProjectAmountApplyService bizProjectAmountApplyService;
    private final ProcessService processService;
    private final RemoteConfigService remoteConfigService;

    @Autowired
    public BizProjectAmountApplyController(IBizBusinessService businessService,
                                           BizProjectAmountApplyService bizProjectAmountApplyService,
                                           ProcessService processService,
                                           RemoteConfigService remoteConfigService) {
        this.businessService = businessService;
        this.bizProjectAmountApplyService = bizProjectAmountApplyService;
        this.processService = processService;
        this.remoteConfigService = remoteConfigService;
    }

    /**
     * 获取项目金额调整申请编号
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("XMTZ" + today + timestamp);
    }

    /**
     * 查询项目详情
     */
    @GetMapping("projectInfo")
    public R projectInfo(String identifier) {
        if (StrUtil.isBlank(identifier)) {
            return R.error("项目编号不能为空");
        }
        String s;
        try {
            Map<String, Object> map = new HashMap<>(2);
            map.put("companyName",SystemUtil.getCompanyName());
            map.put("identifier", identifier);
            SysConfig sysConfig = remoteConfigService.findConfigUrl();
            if ("test".equals(sysConfig.getConfigValue())) {
                s = HttpUtil.get(UrlConstants.JPSELECT_TEST, map);
                log.info("项目金额调整ip_code" + UrlConstants.JPSELECT_TEST + "--" + s);
            } else {
                s = HttpUtil.get(UrlConstants.JPSELECT_ONLINE, map);
                log.info("项目金额调整ip_code" + UrlConstants.JPSELECT_ONLINE + "--" + s);
            }
        } catch (Exception e) {
            log.error("查询失败+ " + e);
            return R.error("未知异常，请联系管理员");
        }
        return JSON.parseObject(s, R.class);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "项目金额调整申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizProjectAmountApply bizProjectAmountApply) {
        try {
            String identifier = bizProjectAmountApply.getIdentifier();
            if (StrUtil.isBlank(identifier)) {
                return R.error("项目编号不能为空！");
            }
            // 判断该项目是否处于审批状态中
            if (bizProjectAmountApplyService.isProcessing(identifier)) {
                return R.error("项目" + identifier + "处于审批状态中，请等待处理完成！");
            }
            if (bizProjectAmountApplyService.insert(bizProjectAmountApply) == 0) {
                return R.error("提交申请失败");
            }
            return R.ok("提交申请成功");
        } catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }

    }

    /**
     * 查询项目金额修改列表
     */
    @ApiOperation(value = "查询项目金额修改列表", notes = "查询项目金额修改列表")
    @GetMapping("list")
    public R list(BizProjectAmountApplyDto bizProjectAmountApplyDto) {
        startPage();
        return result(bizProjectAmountApplyService.findBizProjectAmountApplyList(bizProjectAmountApplyDto));
    }

    /**
     * 获取详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            if (null == business) {
                return R.error("获取流程信息失败");
            }
            BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyService.selectById(Long.valueOf(business.getTableId()));
            bizProjectAmountApply.setTitle(business.getTitle());
            return R.data(bizProjectAmountApply);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }

    /**
     * 项目金额调整导出
     */
    @GetMapping("export_pdf")
    @OperLog(title = "项目金额调整PDF导出", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "项目金额调整PDF导出", notes = "项目金额调整PDF导出")
    public R downlodPdf(String businessKey) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            if (null == business) {
                return R.error("获取pdf失败");
            }
            BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyService.selectById(Long.valueOf(business.getTableId()));
//            reimburseApply.setTitle(business.getTitle());
            //流程
            List<Map<String, Object>> adjust = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
            bizProjectAmountApply.setHiTaskVos(adjust);
            bizProjectAmountApply.setPdfCreateTime(DateUtil.format(bizProjectAmountApply.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            Map<String, Object> map = new HashMap<>(32);
            JSONObject jsonObject2 = new JSONObject(bizProjectAmountApply);
            jsonObject2.set("applyer", business.getTitle().split("提交")[0]);

            jsonObject2.set("printer", SystemUtil.getUserNameCn());
            map.put("key", jsonObject2);
            JSONObject josmmap = JSONUtil.parseObj(map);
            Object object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/salary_adjustment_req", josmmap.toString());

            return R.data(object);
        } catch (Exception e) {
            logger.error("项目金额调整PDF导出失败", e);
            return R.error("项目金额调整PDF导出失败");
        }
    }


    /**
     * 修改状态失败项目重新同步
     */
    @ApiOperation(value = "修改状态失败项目重新同步", notes = "修改状态失败项目重新同步")
    @PostMapping("updateAgain")
    public R updateAgain(@RequestBody BizProjectAmountApplyDto bizProjectAmountApplyDto) {
        return R.ok(bizProjectAmountApplyService.updateAgain(bizProjectAmountApplyDto));
    }
}
