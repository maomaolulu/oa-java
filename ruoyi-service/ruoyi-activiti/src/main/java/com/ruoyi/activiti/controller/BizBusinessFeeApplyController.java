package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.nbcb.sdk.aes.utils.SecurityUtils;
import com.ruoyi.activiti.domain.dto.BizBusinessFeeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizBusinessFeeApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.service.BizBusinessFeeApplyService;
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
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务费申请
 *
 * @author zh
 * @date 2021-12-17
 * @menu 业务费申请
 */

@RestController
@RequestMapping("business_fee_apply")
public class BizBusinessFeeApplyController extends BaseController {


    private final BizBusinessFeeApplyService bizBusinessFeeApplyService;
    private final IBizBusinessService businessService;
    private final IBizAuditService iBizAuditService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final ProcessService processService;

    public BizBusinessFeeApplyController(BizBusinessFeeApplyService bizBusinessFeeApplyService, IBizBusinessService businessService, IBizAuditService iBizAuditService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, ProcessService processService) {
        this.bizBusinessFeeApplyService = bizBusinessFeeApplyService;
        this.businessService = businessService;
        this.iBizAuditService = iBizAuditService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.processService = processService;
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
        return R.data("YW" + today + timestamp);
    }

    /**
     * 根据key查询详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz(@PathVariable("businessKey") String businessKey) {
        try {
            return R.ok("ok", bizBusinessFeeApplyService.selectOne(businessKey));
        } catch (Exception e) {
            System.err.println(e);
            return R.error("查询业务费详情失败");
        }
    }

    /**
     * 业务费申请导出pdf
     *
     * @param businessKey
     * @return
     */
    @SneakyThrows
    @GetMapping("downlodPdf")
    @OperLog(title = "业务费申请导出pdf", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "业务费申请导出pdf")
    public void downlodPdf(String businessKey, HttpServletResponse response) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (StringUtils.isNotNull(business)) {
            BizBusinessFeeApplyDto bizBusinessFeeApplyDto = bizBusinessFeeApplyService.selectOne(businessKey);
            bizBusinessFeeApplyDto.setTitle(business.getTitle());
            //流程
            List<Map<String, Object>> purchase = processService.getPdfProcessAll(business.getProcDefKey(), business.getTableId());
            if (StringUtils.isNotEmpty(purchase)) {
                for (Map<String, Object> temp : purchase) {
                    temp.put("title", "审批流程");
                }
            }
            bizBusinessFeeApplyDto.setPdfCreateTime(DateUtil.format(bizBusinessFeeApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            //打印日期
            bizBusinessFeeApplyDto.setUpdateTime(DateUtils.getNowDate());
            //申请人
            bizBusinessFeeApplyDto.setCreateBy(business.getTitle().split("提交")[0]);

            //业务费申请 导出
            Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(bizBusinessFeeApplyDto), new TypeReference<Map<String, Object>>() {
            });
            map.put("list", purchase);
            XWPFDocument doc = WordExportUtil.exportWord07("template/business_fee_apply.docx", map);
            //合并单元格-审批流程-获取第一个表格
            XWPFTable xwpfTable = doc.getTables().get(0);
            Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
            Word2PdfUtils.toPdfDownload(doc, "业务费申请-" + DateUtils.dateTimeNow(), response);
        }
    }

    /**
     * 查询业务费申请分页
     */
    @ApiOperation(value = "查询业务费申请分页", notes = "查询业务费申请分页")
    @GetMapping("list/purchaseFee")
//    @HasPermissions("purchase:list:goodsAll")
    public R listPurchaseFee(BizBusinessFeeApplyDto dto) {
        startPage();
        return result(bizBusinessFeeApplyService.selectBizBusinessFeeApplyListAll(dto));
    }

    /**
     * 修改业务费申请
     */
    @OperLog(title = "修改业务费申请信息", businessType = BusinessType.UPDATE)
    @PostMapping("update/purchase")
    @ApiOperation(value = "修改业务费申请信息", notes = "修改业务费申请信息")
    public R editSave(@RequestBody BizBusinessFeeApply bizBusinessFeeApply) {
        return R.ok("修改采购物品信息", bizBusinessFeeApplyService.updateBizBusinessFeeApply(bizBusinessFeeApply));
    }

    /**
     * 新增业务费申请
     */
    @ApiOperation(value = "新增业务费申请", notes = "新增业务费申请")
    @OperLog(title = "新增业务费申请", businessType = BusinessType.INSERT)
    @PostMapping("save")
    public R addSave(@RequestBody BizBusinessFeeApply bizBusinessFeeApply) {
        return bizBusinessFeeApplyService.insertBizPurchaseApply(bizBusinessFeeApply);
    }


    /**
     * 删除业务费申请
     */
    @OperLog(title = "删除业务费申请信息", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除业务费申请信息", notes = "删除业务费申请信息")
    @PostMapping("remove")
    public R remove(@RequestBody Integer[] ids) {
        try {
            bizBusinessFeeApplyService.deleteBizBusinessFeeApplyByIds(ids);
            return R.ok("删除成功");
        } catch (Exception e) {
            System.err.println(e);
            return R.error("系统异常，请联系管理员");
        }

    }

}
