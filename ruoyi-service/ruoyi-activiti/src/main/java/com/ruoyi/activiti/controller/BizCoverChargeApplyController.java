package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.dto.BizCoverChargeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeApply;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeInfo;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizCoverChargeApplyService;
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
 * 服务费申请
 *
 * @author zx
 * @date 2022-3-16 22:11:26
 * @menu 服务申请
 */
@RestController
@RequestMapping("cover_apply")
public class BizCoverChargeApplyController extends BaseController {
    private final IBizBusinessService bizBusinessService;
    private final BizCoverChargeApplyService coverChargeApplyService;
    private final IBizAuditService iBizAuditService;
    private final ProcessService processService;


    @Autowired
    public BizCoverChargeApplyController(IBizBusinessService bizBusinessService, BizCoverChargeApplyService coverChargeApplyService, IBizAuditService iBizAuditService, ProcessService processService) {
        this.bizBusinessService = bizBusinessService;
        this.coverChargeApplyService = coverChargeApplyService;
        this.iBizAuditService = iBizAuditService;
        this.processService = processService;
    }


    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizCoverChargeApplyDto coverChargeApplyDto = coverChargeApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        coverChargeApplyDto.setTitle(bizBusiness.getTitle());
        return R.ok("ok", coverChargeApplyDto);
    }

    /**
     * 服务费申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "服务费申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "服务费申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizCoverChargeApplyDto coverChargeApplyDto = coverChargeApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        coverChargeApplyDto.setTitle(bizBusiness.getTitle());

        //申请人
        coverChargeApplyDto.setCreateBy(bizBusiness.getTitle().split("提交")[0]);
        //流程
        List<Map<String, Object>> purchase = processService.getPdfProcessAll(bizBusiness.getProcDefKey(), bizBusiness.getTableId());
        if (StringUtils.isNotEmpty(purchase)) {
            for (Map<String, Object> temp : purchase) {
                temp.put("title", "审批流程");
            }
        }
        //付款方式（1对公（有发票）、2对私（无发票）、3其他）
        String paymentMode = coverChargeApplyDto.getPaymentMode();
        if ("1".equals(paymentMode)) {
            paymentMode = "对公（有发票）";
        } else if ("2".equals(paymentMode)) {
            paymentMode = "对私（无发票）";
        } else {
            paymentMode = "其他";
        }
        coverChargeApplyDto.setPaymentMode(paymentMode);
        coverChargeApplyDto.setPdfCreateTime(DateUtil.format(coverChargeApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        coverChargeApplyDto.setPdfPaymentDate(DateUtil.format(coverChargeApplyDto.getPaymentDate(), "yyyy-MM-dd HH:mm:ss"));
        //总金额转为大写
        String s = Convert.digitToChinese(coverChargeApplyDto.getAmountTotal());
        List<BizCoverChargeInfo> coverChargeInfos = coverChargeApplyDto.getCoverChargeInfos();
        coverChargeApplyDto.setBizReviewInfos(coverChargeInfos);
        coverChargeApplyDto.setPdfAmountTotal(coverChargeApplyDto.getAmountTotal().toString() + "(" + s + ")");


        //服务费申请 导出
        Map<String, Object> map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(coverChargeApplyDto), new TypeReference<Map<String, Object>>() {
        });
        map.put("list", purchase);
        XWPFDocument doc = WordExportUtil.exportWord07("template/cover_apply.docx", map);
        //合并单元格-审批流程-获取第一个表格
        XWPFTable xwpfTable = doc.getTables().get(0);
        Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
        Word2PdfUtils.toPdfDownload(doc, "服务费申请-" + DateUtils.dateTimeNow(), response);
    }

    /**
     * 查询审批费用申请信息
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "查询审批费用申请信息id", required = true)})
    @GetMapping("get/{id}")
    public R get(@PathVariable("id") Long id) {
        BizCoverChargeApplyDto coverChargeApplyDto = coverChargeApplyService.selectBizCoverChargeApplyById(id);
        return R.ok("ok", coverChargeApplyService.getPurchase(coverChargeApplyDto));
    }

    /**
     * 获取服务费编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("FW" + today + timestamp);
    }

    /**
     * 查询服务费列表
     */
    @ApiOperation(value = "查询服务费列表", notes = "查询服务费列表")
    @GetMapping("list")
    public R list(BizCoverChargeApplyDto coverChargeApplyDto) {
        startPage();
        return result(coverChargeApplyService.selectBizCoverChargeApply(coverChargeApplyDto));
    }


    /**
     * 新增保存服务费
     */
    @OperLog(title = "新增保存服务费", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存服务费", notes = "新增保存服务费")
    @PostMapping("save")
    public R addSave(@RequestBody BizCoverChargeApply coverChargeApply) {
        return coverChargeApplyService.insertBizCoverChargeApply(coverChargeApply);
    }


}