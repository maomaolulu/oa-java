package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.fiance.BizReimburseApply;
import com.ruoyi.activiti.domain.fiance.BizReimburseDetail;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizReimburseApplyService;
import com.ruoyi.activiti.service.IBizAuditService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.activiti.utils.Word2PdfUtils;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报销申请
 *
 * @author zx
 * @date 2021/12/16 10:57
 * @menu 报销申请
 */
@RestController
@RequestMapping("reimburse_apply")
public class BizReimburseApplyController extends BaseController {

    private final IBizBusinessService businessService;
    private final BizReimburseApplyService reimburseApplyService;
    private final IBizAuditService iBizAuditService;
    private final ProcessService processService;

    @Autowired
    public BizReimburseApplyController(IBizBusinessService businessService, BizReimburseApplyService reimburseApplyService, IBizAuditService iBizAuditService, ProcessService processService) {
        this.businessService = businessService;
        this.reimburseApplyService = reimburseApplyService;
        this.iBizAuditService = iBizAuditService;
        this.processService = processService;
    }

    @GetMapping
    public R getOwnList(String proDefId) {
        return R.ok();
    }

    /**
     * 获取报销编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("BX" + today + timestamp);
    }

    /**
     * 获取关联审批单下拉列表
     *
     * @return
     */
    @GetMapping("audit_list")
    public R getAuditList(@RequestParam(value = "source", required = false) String source, @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        try {
            return R.data(reimburseApplyService.getAuditList(source, type));
        } catch (Exception e) {
            logger.error("获取关联审批单下拉列表失败", e);
            return R.error("获取关联审批单下拉列表失败");
        }
    }

    /**
     * 新增申请
     */
    @OperLog(title = "报销申请", businessType = BusinessType.INSERT)
    @PostMapping("save")
    public R addSave(@RequestBody BizReimburseApply reimburseApply) {
        return reimburseApplyService.insert(reimburseApply);
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
            BizReimburseApply reimburseApply = reimburseApplyService.selectBizReimburseApplyById(Long.valueOf(business.getTableId()));
            reimburseApply.setTitle(business.getTitle());
            return R.data(reimburseApply);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }

    /**
     * 报销申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @GetMapping("downlodPdf")
    @OperLog(title = "报销申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "报销申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            BizReimburseApply reimburseApply = reimburseApplyService.selectBizReimburseApplyById(Long.valueOf(business.getTableId()));
            //流程
            List<Map<String, Object>> purchase = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
            reimburseApply.setPdfCreateTime(DateUtil.format(reimburseApply.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            //报销申请
            Map<String, Object> map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(reimburseApply), new TypeReference<Map<String, Object>>() {
            });
            //审批流程
            if (StringUtils.isNotEmpty(purchase)) {
                for (Map<String, Object> temp : purchase) {
                    temp.put("title", "审批流程");
                }
            }
            //关联审批单
            List<Map<String, Object>> associateApply = reimburseApply.getAssociateApply();
            if (StringUtils.isNotEmpty(associateApply)) {
                StringBuilder associateTitle = new StringBuilder();
                for (Map<String, Object> mapTemp : associateApply) {
                    Object title = mapTemp.get("associateTitle");
                    associateTitle.append(title).append("    ");
                }
                map.put("associateTitle", associateTitle.toString());
            }
            map.put("list", purchase);
            map.put("updateTime", DateUtils.getTime());
            //报销明细
            List<BizReimburseDetail> reimburseDetails = reimburseApply.getReimburseDetails();
            if (StringUtils.isNotEmpty(reimburseDetails)) {
                for (BizReimburseDetail detail : reimburseDetails) {
                    detail.setCreateBy(DateUtil.format(detail.getExpenseTime(), "yyyy-MM-dd"));
                }
                map.put("details", reimburseDetails);
            }
            XWPFDocument doc = WordExportUtil.exportWord07("template/reimburse_apply.docx", map);
            //合并单元格-审批流程-获取第一个表格
            XWPFTable xwpfTable = doc.getTables().get(0);
            Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 11);
            Word2PdfUtils.toPdfDownload(doc, "报销申请-" + DateUtils.dateTimeNow(), response);
        } catch (Exception e) {
            logger.error("获取报销详情pdf失败", e);
        }

    }
}
