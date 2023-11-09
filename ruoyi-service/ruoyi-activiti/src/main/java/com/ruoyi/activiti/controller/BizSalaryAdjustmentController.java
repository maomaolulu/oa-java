package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.my_apply.BizSalaryAdjustment;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizSalaryAdjustmentService;
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
 * 薪资调整
 *
 * @author zx
 * @date 2022/3/8 22:29
 * @menu 薪资调整
 */
@RestController
@RequestMapping("salary_adjustment")
public class BizSalaryAdjustmentController extends BaseController {
    private final IBizBusinessService businessService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final BizSalaryAdjustmentService salaryAdjustmentService;
    private final ProcessService processService;

    @Autowired
    public BizSalaryAdjustmentController(IBizBusinessService businessService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService, BizSalaryAdjustmentService salaryAdjustmentService, ProcessService processService) {
        this.businessService = businessService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.salaryAdjustmentService = salaryAdjustmentService;
        this.processService = processService;
    }

    /**
     * 获取薪资调整申请编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("XZTZ" + today + timestamp);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "薪资调整申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizSalaryAdjustment salaryAdjustment) {
        try {
            salaryAdjustment.setCreateTime(new Date());
            salaryAdjustment.setCreateBy(SystemUtil.getUserId().toString());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyName = belongCompany2.get("companyName").toString();

            int insert = salaryAdjustmentService.insert(salaryAdjustment);
            if (insert == 0) {
                return R.error("提交申请失败");
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
            BizSalaryAdjustment salaryAdjustment = salaryAdjustmentService.selectById(Long.valueOf(business.getTableId()));
            salaryAdjustment.setTitle(business.getTitle());
            return R.data(salaryAdjustment);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }

    /**
     * 薪资调整导出
     */
    @SneakyThrows
    @GetMapping("export_pdf")
    @OperLog(title = "薪资调整PDF导出", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "薪资调整PDF导出", notes = "薪资调整PDF导出")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (StringUtils.isNotNull(business)) {
            BizSalaryAdjustment salaryAdjustment = salaryAdjustmentService.selectById(Long.valueOf(business.getTableId()));
            //流程
            List<Map<String, Object>> adjust = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
            if (StringUtils.isNotEmpty(adjust)) {
                for (Map<String, Object> temp : adjust) {
                    temp.put("title", "审批流程");
                }
            }
            salaryAdjustment.setPdfCreateTime(DateUtil.format(salaryAdjustment.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            //申请人
            salaryAdjustment.setCreateBy(business.getTitle().split("提交")[0]);
//            Map<String, Object> map = new HashMap<>(32);
//            JSONObject jsonObject2 = new JSONObject(salaryAdjustment);
//            jsonObject2.set("applyer", business.getTitle().split("提交")[0]);
//            jsonObject2.set("printer", SystemUtil.getUserNameCn());
//            map.put("key", jsonObject2);
//            JSONObject josmmap = JSONUtil.parseObj(map);
//            Object object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/salary_adjustment_req", josmmap.toString());
            //薪资调整 导出
            Map<String, Object> map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(salaryAdjustment), new TypeReference<Map<String, Object>>() {
            });
            map.put("list", adjust);
            map.put("pdfName", SystemUtil.getUserNameCn());
            map.put("updateTime", DateUtils.getTime());
            XWPFDocument doc = WordExportUtil.exportWord07("template/salary_adjustment.docx", map);
            //合并单元格-审批流程-获取第一个表格
            XWPFTable xwpfTable = doc.getTables().get(0);
            Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
            Word2PdfUtils.toPdfDownload(doc, "薪资调整单-" + DateUtils.dateTimeNow(), response);
        }


    }


}
