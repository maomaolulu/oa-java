package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.asset.BizScrappedApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizScrappedApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.activiti.utils.Word2PdfUtils;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.feign.RemoteDictService;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报废出库申请
 *
 * @description: 报废出库申请
 * @author: zx
 * @date: 2021/11/21 19:10
 * @menu 报废出库申请
 */
@RestController
@RequestMapping("scrapped_apply")
public class BizScrappedApplyController extends BaseController {

    private final BizScrappedApplyService scrappedApplyService;
    private final IBizBusinessService businessService;
    private final ProcessService processService;
    private final RemoteDictService remoteDictService;

    @Autowired
    public BizScrappedApplyController(BizScrappedApplyService scrappedApplyService, IBizBusinessService businessService, ProcessService processService, RemoteDictService remoteDictService) {
        this.scrappedApplyService = scrappedApplyService;
        this.businessService = businessService;
        this.processService = processService;
        this.remoteDictService = remoteDictService;
    }

    /**
     * 新增报废出库申请
     */
    @ApiOperation(value = "新增报废出库申请", notes = "")
    @PostMapping("save")
    @OperLog(title = "报废出库申请", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody BizScrappedApply scrappedApply) {
        return scrappedApplyService.insert(scrappedApply);
    }

    /**
     * 查询详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    public R biz(@PathVariable("businessKey") String businessKey) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (null != business) {
            BizScrappedApply scrappedApply = scrappedApplyService.selectBizScrappedApplyById(business.getTableId());
            scrappedApply.setApplyer(business.getApplyer());
            scrappedApply.setTitle(business.getTitle());
            return R.ok("ok", scrappedApply);
        }
        return R.error("no record");
    }

    /**
     * 通过资产id 查询详情
     *
     * @param assetId
     * @return
     */
    @GetMapping("biz/asset")
    public R biz(Long assetId) {
        try {
            Map<String, Object> map = new HashMap<>(2);
            BizBusiness business = businessService.selectBizBusinessByAssetId(assetId);
            if (business == null) {
                return R.data(null);
            }
            if (null != business) {
                BizScrappedApply scrappedApply = scrappedApplyService.selectBizScrappedApplyById(business.getTableId());
                scrappedApply.setApplyer(business.getApplyer());
                scrappedApply.setTitle(business.getTitle());
                map.put("info", scrappedApply);
            } else {
                map.put("info", "");
            }
            map.put("history", processService.getProcessAll("scrapped", business.getTableId(), SystemUtil.getUserId(), null, null, business.getProcInstId()));
            return R.data(map);
        } catch (Exception e) {
            return R.error("查询报废详情失败");
        }
    }

    /**
     * 报废出库申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "报废出库申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "报废出库申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);

        BizScrappedApply scrappedApply = scrappedApplyService.selectBizScrappedApplyById(business.getTableId());
        scrappedApply.setApplyer(business.getApplyer());
        scrappedApply.setTitle(business.getTitle());
        //流程
        List<Map<String, Object>> purchase = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
        if (StringUtils.isNotEmpty(purchase)) {
            for (Map<String, Object> temp : purchase) {
                temp.put("title", "审批流程");
            }
        }
        scrappedApply.setPdfCreateTime(DateUtil.format(scrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        //采购日期
        String format = DateUtil.format(scrappedApply.getAsset().getPurchaseTime(), DatePattern.NORM_DATE_PATTERN);
        scrappedApply.getAsset().setPdfPurchaseTime(format);
        String labelByCode = remoteDictService.getLabelByCode(scrappedApply.getAsset().getAssetType());
        scrappedApply.getAsset().setAssetType(labelByCode);

        Date date = new Date();
        long betweenDay = DateUtil.betweenDay(scrappedApply.getAsset().getPurchaseTime(), date, true);
        long betweenMonth = DateUtil.betweenMonth(scrappedApply.getAsset().getPurchaseTime(), date, true);
        long betweenYear = DateUtil.betweenYear(scrappedApply.getAsset().getPurchaseTime(), date, true);
        //打印时间
        scrappedApply.setUpdateTime(date);
        scrappedApply.setPdfDate(betweenYear + "年" + betweenMonth + "月" + betweenDay + "天");

        //采购申请 导出
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(scrappedApply), new TypeReference<Map<String, Object>>() {
        });

        map.put("list", purchase);
        XWPFDocument doc = WordExportUtil.exportWord07("template/scrapped_apply.docx", map);
        //合并单元格-审批流程-获取第一个表格
        XWPFTable xwpfTable = doc.getTables().get(0);
        Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
        Word2PdfUtils.toPdfDownload(doc, "报废出库申请-" + DateUtils.dateTimeNow(), response);

    }


    @GetMapping("test/{deptId}")
    public R test(@PathVariable("deptId") Long deptId) {

        scrappedApplyService.test(deptId);
        return R.error("no record");
    }
}
