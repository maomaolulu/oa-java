package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.dto.BizReviewApplyDto;
import com.ruoyi.activiti.domain.dto.ReviewInfoEditDto;
import com.ruoyi.activiti.domain.fiance.BizReviewApply;
import com.ruoyi.activiti.domain.fiance.BizReviewInfo;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizReviewApplyService;
import com.ruoyi.activiti.service.IBizAuditService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.activiti.utils.Word2PdfUtils;
import com.ruoyi.common.auth.annotation.HasPermissions;
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
 * 评审费/服务费申请
 *
 * @author zh
 * @date 2021-12-20
 * @menu 评审服务申请
 */
@RestController
@RequestMapping("review_apply")
public class BizReviewApplyController extends BaseController {
    private final IBizBusinessService bizBusinessService;
    private final BizReviewApplyService bizReviewApplyService;
    private final IBizAuditService iBizAuditService;
    private final ProcessService processService;


    @Autowired
    public BizReviewApplyController(IBizBusinessService bizBusinessService, BizReviewApplyService bizReviewApplyService, IBizAuditService iBizAuditService, ProcessService processService) {
        this.bizBusinessService = bizBusinessService;
        this.bizReviewApplyService = bizReviewApplyService;
        this.iBizAuditService = iBizAuditService;
        this.processService = processService;
    }


    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizReviewApplyDto bizReviewApplyDto = bizReviewApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        bizReviewApplyDto.setTitle(bizBusiness.getTitle());
        return R.ok("ok", bizReviewApplyDto);
    }

    /**
     * 评审服务申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "评审服务申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "评审服务申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        BizReviewApplyDto bizReviewApplyDto = bizReviewApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
        bizReviewApplyDto.setTitle(bizBusiness.getTitle());
        //申请人
        bizReviewApplyDto.setCreateBy(bizBusiness.getTitle().split("提交")[0]);

        //状态
        List<BizReviewInfo> bizReviewInfos = bizReviewApplyDto.getBizReviewInfos();
        if (StringUtils.isNotEmpty(bizReviewInfos)) {
            bizReviewInfos.forEach(temp -> {
                String delFlag = temp.getDelFlag();
                if ("0".equals(delFlag)) {
                    temp.setDelFlag("正常");
                } else {
                    temp.setDelFlag("取消");
                }
            });
        }
        //流程
        List<Map<String, Object>> purchase = processService.getPdfProcessAll(bizBusiness.getProcDefKey(), bizBusiness.getTableId());
        if (StringUtils.isNotEmpty(purchase)) {
            for (Map<String, Object> temp : purchase) {
                temp.put("title", "审批流程");
            }
        }
        //付款方式（1对公（有发票）、2对私（无发票）、3其他）
        String paymentMode = bizReviewApplyDto.getPaymentMode();
        if ("1".equals(paymentMode)) {
            paymentMode = "对公（有发票）";
        } else if ("2".equals(paymentMode)) {
            paymentMode = "对私（无发票）";
        } else {
            paymentMode = "其他";
        }
        bizReviewApplyDto.setPaymentMode(paymentMode);
        bizReviewApplyDto.setPdfCreateTime(DateUtil.format(bizReviewApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        bizReviewApplyDto.setPdfPaymentDate(DateUtil.format(bizReviewApplyDto.getPaymentDate(), "yyyy-MM-dd HH:mm:ss"));
        //总金额转为大写
        String s = Convert.digitToChinese(bizReviewApplyDto.getAmountTotal());
        bizReviewApplyDto.setPdfAmountTotal(bizReviewApplyDto.getAmountTotal().toString() + "(" + s + ")");
//			Map<String, Object> map = new HashMap<>();
//			JSONObject jsonObject2 = new JSONObject(bizReviewApplyDto);
//			map.put("key",jsonObject2);
//			JSONObject josmmap = JSONUtil.parseObj(map);
//			Object object=null;
//			if(bizReviewApplyDto.getTypes().equals("1")){
//				//评审费用
//				 object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/review_tip_req",josmmap.toString());
//			}else if(bizReviewApplyDto.getTypes().equals("2")){
//				//服务费用
//				object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/service_tip_req",josmmap.toString());
//			}else if(bizReviewApplyDto.getTypes().equals("3")){
//				//其他费用
//				object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/other_tip_req",josmmap.toString());
//			}
//			Object object = HttpUtil.post("https://coa.anliantest.com/oaProxyApi/proxyPython/utility/pdf/market_tip_req",josmmap.toString());
        //评审服务申请 导出
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(bizReviewApplyDto), new TypeReference<Map<String, Object>>() {
        });
        map.put("list", purchase);
        XWPFDocument doc = WordExportUtil.exportWord07("template/review_apply.docx", map);
        //合并单元格-审批流程-获取第一个表格
        XWPFTable xwpfTable = doc.getTables().get(0);
        Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
        Word2PdfUtils.toPdfDownload(doc, "评审服务申请-" + DateUtils.dateTimeNow(), response);
    }

    /**
     * 查询审批费用申请信息
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "查询审批费用申请信息id", required = true)})
    @GetMapping("get/{id}")
    public R get(@PathVariable("id") Long id) {
        BizReviewApplyDto bizReviewApplyDto = bizReviewApplyService.selectBizReviewApplyById(id);
        return R.ok("ok", bizReviewApplyService.getPurchase(bizReviewApplyDto));
    }

    /**
     * 获取评审服务编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("PS" + today + timestamp);
    }

    /**
     * 查询评审服务列表
     */
    @ApiOperation(value = "查询评审服务列表", notes = "查询评审服务列表")
    @GetMapping("list")
    public R list(BizReviewApplyDto bizReviewApplyDto) {
        startPage();
        return result(bizReviewApplyService.selectBizReviewApply(bizReviewApplyDto));
    }


    /**
     * 新增保存评审服务
     */
    @OperLog(title = "新增保存评审服务", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存评审服务", notes = "新增保存评审服务")
    @PostMapping("save")
    public R addSave(@RequestBody BizReviewApply bizReviewApply) {
        return bizReviewApplyService.insertBizReviewApply(bizReviewApply);
    }

    /**
     * 编辑评审明细
     */
    @OperLog(title = "编辑评审明细", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "编辑评审明细", notes = "编辑评审明细")
    @PostMapping("info")
    @HasPermissions("financial:servicefee:editDate")
    public R editInfo(@RequestBody ReviewInfoEditDto reviewInfoEditDto) {
        try {

            bizReviewApplyService.editInfo(reviewInfoEditDto.getBizReviewInfos(), reviewInfoEditDto.getTaskId());
            return R.ok();
        } catch (Exception e) {
            logger.error("编辑评审明细", e);
            return R.error("编辑评审明细失败");
        }
    }

}