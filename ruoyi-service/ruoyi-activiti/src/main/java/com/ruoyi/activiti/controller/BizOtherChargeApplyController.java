package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.dto.BizOtherChargeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizOtherChargeApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizOtherChargeApplyService;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
 * 其他费用申请
 *
 * @author zh
 * @date 2021-12-20
 * @menu 其他费用申请
 */
@RestController
@RequestMapping("other_apply")
public class BizOtherChargeApplyController extends BaseController {
    private final IBizBusinessService bizBusinessService;
    private final BizOtherChargeApplyService otherChargeApplyService;
    private final IBizAuditService iBizAuditService;
    private final ProcessService processService;


    @Autowired
    public BizOtherChargeApplyController(IBizBusinessService bizBusinessService, BizOtherChargeApplyService otherChargeApplyService, IBizAuditService iBizAuditService, ProcessService processService) {
        this.bizBusinessService = bizBusinessService;
        this.otherChargeApplyService = otherChargeApplyService;
        this.iBizAuditService = iBizAuditService;
        this.processService = processService;
    }


    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizOtherChargeApplyDto otherChargeApplyDto = otherChargeApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        otherChargeApplyDto.setTitle(bizBusiness.getTitle());
        return R.ok("ok", otherChargeApplyDto);
    }

    /**
     * 其他费用申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "其他费用申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "其他费用申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizOtherChargeApplyDto otherChargeApplyDto = otherChargeApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        otherChargeApplyDto.setTitle(bizBusiness.getTitle());
        //申请人
        otherChargeApplyDto.setCreateBy(bizBusiness.getTitle().split("提交")[0]);
        //流程
        List<Map<String, Object>> purchase = processService.getPdfProcessAll(bizBusiness.getProcDefKey(), bizBusiness.getTableId());
        otherChargeApplyDto.setHiTaskVos(purchase);

        otherChargeApplyDto.setPdfCreateTime(DateUtil.format(otherChargeApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        otherChargeApplyDto.setPdfPaymentDate(DateUtil.format(otherChargeApplyDto.getPaymentDate(), "yyyy-MM-dd HH:mm:ss"));
        //总金额转为大写
        String s = Convert.digitToChinese(otherChargeApplyDto.getAmountTotal());
        otherChargeApplyDto.setPdfAmountTotal(otherChargeApplyDto.getAmountTotal().toString() + "(" + s + ")");
        otherChargeApplyDto.setBizReviewInfos(otherChargeApplyDto.getOtherChargeInfos());

        //其他费用
        Map<String, Object> map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(otherChargeApplyDto), new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> hiTaskVos = otherChargeApplyDto.getHiTaskVos();
        if (StringUtils.isNotEmpty(hiTaskVos)) {
            for (Map<String, Object> temp : hiTaskVos) {
                temp.put("title", "审批流程");
            }
        }
        map.put("list", hiTaskVos);
        XWPFDocument doc = WordExportUtil.exportWord07("template/other_apply.docx", map);
        //合并单元格-审批流程-获取第一个表格
        XWPFTable xwpfTable = doc.getTables().get(0);
        Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 11);
        Word2PdfUtils.toPdfDownload(doc, "其他费用申请-" + DateUtils.dateTimeNow(), response);

    }

    /**
     * 查询审批费用申请信息
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "查询审批费用申请信息id", required = true)})
    @GetMapping("get/{id}")
    public R get(@PathVariable("id") Long id) {
        BizOtherChargeApplyDto otherChargeApplyDto = otherChargeApplyService.selectBizOtherChargeApplyById(id);
        return R.ok("ok", otherChargeApplyService.getPurchase(otherChargeApplyDto));
    }

    /**
     * 获取其他费用编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("QT" + today + timestamp);
    }

    /**
     * 查询其他费用列表
     */
    @ApiOperation(value = "查询其他费用列表", notes = "查询其他费用列表")
    @GetMapping("list")
    public R list(BizOtherChargeApplyDto otherChargeApplyDto) {
        startPage();
        return result(otherChargeApplyService.selectBizOtherChargeApply(otherChargeApplyDto));
    }


    /**
     * 新增保存其他费用
     */
    @OperLog(title = "新增保存其他费用", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存其他费用", notes = "新增保存其他费用")
    @PostMapping("save")
    public R addSave(@RequestBody BizOtherChargeApply otherChargeApply) {
        return otherChargeApplyService.insertBizOtherChargeApply(otherChargeApply);
    }


}