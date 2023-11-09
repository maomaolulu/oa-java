package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.fiance.BizPaymentApply;
import com.ruoyi.activiti.domain.fiance.BizPaymentDetail;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizPaymentApplyService;
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
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 付款申请
 *
 * @author zx
 * @menu 付款申请
 * @date 2021/12/19 20:10
 */
@RestController
@RequestMapping("payment_apply")
public class BizPaymentApplyController extends BaseController {
    private final IBizBusinessService businessService;
    private final BizPaymentApplyService paymentApplyService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final IBizAuditService iBizAuditService;
    private final ProcessService processService;


    @Autowired
    public BizPaymentApplyController(IBizBusinessService businessService, BizPaymentApplyService paymentApplyService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService, IBizAuditService iBizAuditService, ProcessService processService) {
        this.businessService = businessService;
        this.paymentApplyService = paymentApplyService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.iBizAuditService = iBizAuditService;
        this.processService = processService;
    }

    /**
     * 获取付款编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("FK" + today + timestamp);
    }

    /**
     * 获取关联审批单下拉列表
     *
     * @return
     */
    @GetMapping("audit_list")
    public R getAuditList(@RequestParam(value = "source", required = false) String source) {
        try {
            return R.data(paymentApplyService.getAuditList(source));

        } catch (Exception e) {
            logger.error("获取关联审批单下拉列表失败", e);
            return R.error("获取关联审批单下拉列表失败");
        }
    }

    /**
     * 新增申请
     */
    @OperLog(title = "付款申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizPaymentApply paymentApply) {
        try {
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyName = belongCompany2.get("companyName").toString();

            int insert = paymentApplyService.insert(paymentApply);
            if (insert == 0) {
                return R.error("提交申请失败");
            }
            if (insert == 2) {
                return R.error(companyName + "缺少经营参数");
            }
            if (insert == 3) {
                return R.error("付款明细不能为空");
            }
            return R.ok("提交申请成功");
        } catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }

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
            BizPaymentApply paymentApply = paymentApplyService.selectById(Long.valueOf(business.getTableId()));
            paymentApply.setTitle(business.getTitle());
            return R.data(paymentApply);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }

    /**
     * 付款申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "付款申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "付款申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (StringUtils.isNotNull(business)) {
            BizPaymentApply paymentApply = paymentApplyService.selectById(Long.valueOf(business.getTableId()));
            paymentApply.setTitle(business.getTitle());
            //流程
            List<Map<String, Object>> purchase = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
            if (StringUtils.isNotEmpty(purchase)) {
                for (Map<String, Object> temp : purchase) {
                    temp.put("title", "审批流程");
                }
            }
            //关联审批单信息
            StringBuilder str = new StringBuilder();
            List<Map<String, Object>> associateApply = paymentApply.getAssociateApply();
            if (StringUtils.isNotEmpty(associateApply)) {
                for (Map<String, Object> map : associateApply) {
                    str.append(map.get("associateTitle").toString()).append("  ");
                }
            }
            paymentApply.setPdfAssociateApply(str.toString());
            //关联采购单
            StringBuilder pdfPurchaseApply = new StringBuilder();
            List<Map<String, Object>> associatePurchaseApply = paymentApply.getAssociatePurchaseApply();
            if (StringUtils.isNotEmpty(associatePurchaseApply)) {
                for (Map<String, Object> map : associatePurchaseApply) {
                    pdfPurchaseApply.append(map.get("associateTitle").toString()).append("  ");
                }
            }

            //款项类目
            String type = switchProjectType(paymentApply.getProjectType());
            paymentApply.setProjectType(type);
            //申请日期
            paymentApply.setPdfCreateTime(DateUtil.format(paymentApply.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            //打印日期
            paymentApply.setUpdateTime(DateUtils.getNowDate());
            //申请人
            paymentApply.setCreateBy(business.getTitle().split("提交")[0]);
            //填充详情
            List<BizPaymentDetail> paymentDetails = paymentApply.getPaymentDetails();
            if (StringUtils.isNotEmpty(paymentDetails)) {
                int i = 1;
                for (BizPaymentDetail temp : paymentDetails) {
                    temp.setCreateBy("费用明细" + i);
                    i++;
                }
            }
            //付款申请（旧版）-导出
            Map<String, Object> map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(paymentApply), new TypeReference<Map<String, Object>>() {
            });
            map.put("list", purchase);
            map.put("pdfPurchaseApply", pdfPurchaseApply.toString());
            map.put("address", paymentApply.getProvinceName() + paymentApply.getCityName());
            XWPFDocument doc = WordExportUtil.exportWord07("template/payment_apply.docx", map);
            //合并单元格-审批流程-获取第一个表格
            XWPFTable xwpfTable = doc.getTables().get(0);
            Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 20);
            Word2PdfUtils.toPdfDownload(doc, "付款申请-" + DateUtils.dateTimeNow(), response);
        }
    }

    /**
     * 获取款项类目
     */
    private String switchProjectType(String projectType) {
        String result;
        switch (projectType) {
            case "1":
                result = "试剂或耗材";
                break;
            case "2":
                result = "办公用品及办公耗材";
                break;
            case "3":
                result = "警示标牌或广告宣传材料";
                break;
            case "4":
                result = "仪器检定或仪器维修";
                break;
            case "5":
                result = "仪器及办公设备";
                break;
            case "6":
                result = "培训费";
                break;
            case "7":
                result = "标书费或招标代理费";
                break;
            case "8":
                result = "房租或水电费";
                break;
            case "9":
                result = "业务退款";
                break;
            case "10":
                result = "业务分包费";
                break;
            case "11":
                result = "备用金";
                break;
            case "12":
                result = "投资款";
                break;
            case "13":
                result = "其他";
                break;
            default:
                result = " ";
        }
        return result;
    }
}
