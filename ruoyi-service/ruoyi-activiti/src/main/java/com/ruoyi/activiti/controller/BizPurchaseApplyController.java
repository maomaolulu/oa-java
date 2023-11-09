package com.ruoyi.activiti.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.activiti.domain.dto.BizSupplierQuoteDto;
import com.ruoyi.activiti.domain.dto.GoodsRunVariableDto;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.IBizPurchaseApplyService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购申请
 *
 * @author zx
 * @date 2021-11-16
 * @menu 采购申请
 */
@RestController
@RequestMapping("purchase_apply")
public class BizPurchaseApplyController extends BaseController {

    private final IBizPurchaseApplyService bizPurchaseApplyService;
    private final IBizBusinessService businessService;
    private final ProcessService processService;

    @Autowired
    public BizPurchaseApplyController(IBizPurchaseApplyService bizPurchaseApplyService, IBizBusinessService businessService, ProcessService processService) {
        this.bizPurchaseApplyService = bizPurchaseApplyService;
        this.businessService = businessService;
        this.processService = processService;
    }

    @GetMapping("biz/info")
    @ApiOperation(value = "根据businessKey查询")
    public R biz(String businessKey) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (null != business) {
            BizPurchaseApply bizPurchaseApply = bizPurchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(business.getTableId()));
            bizPurchaseApply.setTitle(business.getTitle());
            return R.data(bizPurchaseApply);
        }
        return R.error("查询采购申请信息失败");
    }

    /**
     * 获取采购编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("CG" + today + timestamp);
    }

    /**
     * 根据goodId查询流程详情
     *
     * @param goodId 物品id
     * @return
     */
    @GetMapping("biz/asset")
    @ApiOperation(value = "根据goodId查询流程详情")
    public R biz(Long goodId) {
        try {

            BizBusiness business = businessService.selectBizBusinessByGoodId(goodId);
            if (business == null) {
                return R.data(null);
            }
            BizPurchaseApply bizPurchaseApply = bizPurchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(business.getTableId()));
            bizPurchaseApply.setBizGoodsInfos(bizPurchaseApply.getBizGoodsInfos().stream().filter(g -> g.getId().equals(goodId)).collect(Collectors.toList()));
            bizPurchaseApply.setTitle(business.getTitle());
            HashMap<String, Object> map = new HashMap<>();
            map.put("business", business);
            map.put("purchase", bizPurchaseApply);
            return R.data(map);
        } catch (Exception e) {

            return R.error("查询流程信息失败");
        }
    }

    /**
     * 查询采购申请详情
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (null != business) {
            BizPurchaseApply bizPurchaseApply = bizPurchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(business.getTableId()));
            bizPurchaseApply.setTitle(business.getTitle());

            return R.data(bizPurchaseApply);
        }
        return R.error("查询采购申请信息失败");
    }

    @SneakyThrows
    @GetMapping("downlodPdf")
    @ApiOperation(value = "采购申请导出pdf")
    @OperLog(title = "采购申请导出pdf", businessType = BusinessType.EXPORT)
    public void downlodPdf(String businessKey, HttpServletResponse response) {

        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (null != business) {
            BizPurchaseApply bizPurchaseApply = bizPurchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(business.getTableId()));
            bizPurchaseApply.setTitle(business.getTitle());
//			HiTaskVo hiTaskVo = new HiTaskVo();
//			hiTaskVo.setProcInstId(business.getProcInstId());
//			List<Map<String, Object>> historyTaskMap = iBizAuditService.getHistoryTaskMap(hiTaskVo);
//			Map<String, Object> purchase = processService.getProcessAll("purchase", business.getTableId(), null, null, null);
//			List<ProcessesAllTaskVo> allResult =(List<ProcessesAllTaskVo>) purchase.get("tasks");
            List<Map<String, Object>> purchase = processService.getPdfProcessAll("purchase", business.getTableId());
            bizPurchaseApply.setHiTaskVos(purchase);
            Date createTime = bizPurchaseApply.getCreateTime();
            Date expectDate = bizPurchaseApply.getExpectDate();
            bizPurchaseApply.setPdfCreateTime(DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss"));
            bizPurchaseApply.setUpdateTime(DateUtils.getNowDate());
            if (expectDate != null) {
                bizPurchaseApply.setPdfExpectDate(DateUtil.format(expectDate, "yyyy-MM-dd "));
            }
            //申请人
            bizPurchaseApply.setCreateBy(business.getTitle().split("提交")[0]);
            //增加序号
            List<BizGoodsInfo> bizGoodsInfos = bizPurchaseApply.getBizGoodsInfos();
            if (StringUtils.isNotEmpty(bizGoodsInfos)) {
                for (int i = 0; i < bizGoodsInfos.size(); i++) {
                    bizGoodsInfos.get(i).setDeptId(i + 1);
                }
            }
            //采购申请 导出
            Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(bizPurchaseApply), new TypeReference<Map<String, Object>>() {
            });
            List<Map<String, Object>> hiTaskVos = bizPurchaseApply.getHiTaskVos();
            if (StringUtils.isNotEmpty(hiTaskVos)) {
                for (Map<String, Object> temp : hiTaskVos) {
                    temp.put("title", "审批流程");
                }
            }
            map.put("list", hiTaskVos);
            XWPFDocument doc = WordExportUtil.exportWord07("template/purchase_request_form.docx", map);
            //合并单元格-审批流程-获取第一个表格
            XWPFTable xwpfTable = doc.getTables().get(0);
            Word2PdfUtils.mergeCellsVertically(xwpfTable, 0, 10);
            Word2PdfUtils.toPdfDownload(doc, "采购申请-" + DateUtils.dateTimeNow(), response);
        }
    }


    /**
     * 查询采购申请信息
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "采购申请业务id", required = true)})
    @GetMapping("get/{id}")
    public R get(@PathVariable("id") Long id) {
        BizPurchaseApply bizPurchaseApply = bizPurchaseApplyService.selectBizPurchaseApplyById(id);
        // 抄送人赋值
        return R.data(bizPurchaseApply);

    }

    /**
     * 查询采购物品信息
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "采购物品业务id", required = true)})
    @GetMapping("get/goods/{id}")
    public BizGoodsInfo getGoods(@PathVariable("id") Long id) {
        return bizPurchaseApplyService.selectBizGoodsInfoById(id);

    }

    /**
     * 查询采购物品信息列表分页
     */
    @ApiOperation(value = "查询采购物品信息列表分页", notes = "查询采购物品信息列表分页")
    @GetMapping("list/goods")
    public R listGoods(BizGoodsInfo bizGoodsInfo) {
        startPage();
        return result(bizPurchaseApplyService.selectBizGoodsInfoList(bizGoodsInfo));
    }

    /**
     * 查询采购物品信息列表
     */
    @ApiOperation(value = "查询采购物品信息列表", notes = "查询申请单下采购的物品信息列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "purchaseId", value = "申请单id", required = true)})
    @GetMapping("list/goods_no_page")
    public R getGoodsByParentId(Long purchaseId) {
        BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
        bizGoodsInfo.setPurchaseId(purchaseId);
        return R.ok("查询采购物品信息列表成功", bizPurchaseApplyService.selectBizGoodsInfoList(bizGoodsInfo));
    }

    /**
     * 修改保存采购物品信息
     */
    @PostMapping("update/goods")
    @OperLog(title = "修改保存采购物品信息", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存采购物品信息", notes = "修改保存采购物品信息")
    public R editSave(@RequestBody GoodsRunVariableDto goodsRunVariableDto) {
//        return R.ok("修改保存采购物品信息", bizPurchaseApplyService.updateBizGoodsInfo(goodsRunVariableDto));
        return R.ok();
    }


    /**
     * 查询采购申请(新)列表
     */
    @ApiOperation(value = "查询采购申请列表", notes = "查询采购申请列表")
    @GetMapping("list")
    public R list(BizPurchaseApply bizPurchaseApply) {
        startPage();
        return result(bizPurchaseApplyService.selectBizPurchaseApplyList(bizPurchaseApply));
    }

    /**
     * 根据名称查询耗材详情
     */
    @ApiOperation(value = "根据名称查询耗材详情", notes = "根据名称查询耗材详情")
    @GetMapping("listAaSpuByName")
    public R listAaSpuByName(String name, String deptId) {
        startPage();
        return result(bizPurchaseApplyService.listByName(name, deptId));
    }

    /**
     * 根据id查询耗材详情
     */
    @ApiOperation(value = "根据id查询耗材详情", notes = "根据id查询耗材详情")
    @GetMapping("AaSpuById")
    public R AaSpuById(Long id) {

        return R.data(bizPurchaseApplyService.AaSpuById(id));
    }


    /**
     * 新增保存采购申请(新)
     */
    @ApiOperation(value = "新增保存采购申请", notes = "新增保存采购申请")
    @PostMapping("save")
    @OperLog(title = "采购申请", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody BizPurchaseApply bizPurchaseApply) {
        return bizPurchaseApplyService.insertBizPurchaseApply(bizPurchaseApply);
    }

    /**
     * 修改保存采购申请(新)
     */
    @OperLog(title = "修改保存采购申请", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存采购申请", notes = "修改保存采购申请")
    @PostMapping("update")
    public R editSave(@RequestBody BizPurchaseApply bizPurchaseApply) {
        return toAjax(bizPurchaseApplyService.updateBizPurchaseApply(bizPurchaseApply));
    }

    /**
     * 删除采购申请信息
     */
    @OperLog(title = "删除采购申请信息", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除采购申请信息", notes = "删除采购申请信息")
    @PostMapping("remove")
    public R remove(String[] ids) {
        return toAjax(bizPurchaseApplyService.deleteBizPurchaseApplyByIds(ids));
    }

    /**
     * 保存采购物品供应商信息
     */
    @OperLog(title = "保存采购物品供应商信息", businessType = BusinessType.INSERT)
    @ApiOperation(value = "保存采购物品供应商信息", notes = "保存采购物品供应商信息")
    @PostMapping("quote")
    @HasPermissions("purchase:quote:save")
    public R saveQuote(@RequestBody BizSupplierQuoteDto supplierQuoteDto) {
        try {
            bizPurchaseApplyService.saveQuote(supplierQuoteDto);
            return R.ok("保存成功");
        } catch (Exception e) {
            logger.error("保存采购物品供应商信息失败", e);
            return R.error("保存失败");
        }
    }

    /**
     * 删除采购物品供应商信息
     */
    @OperLog(title = "删除采购物品供应商信息", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除采购物品供应商信息", notes = "删除采购物品供应商信息")
    @GetMapping("quote_del")
    @HasPermissions("purchase:quote:delete")
    public R deleteQuote(Long id, String procInstId) {
        try {
            bizPurchaseApplyService.deleteQuote(id, procInstId);
            return R.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除采购物品供应商信息失败", e);
            return R.error("删除失败");
        }
    }

    /**
     * 获取采购物品供应商信息
     */
    @OperLog(title = "获取采购物品供应商信息", businessType = BusinessType.DELETE)
    @ApiOperation(value = "获取采购物品供应商信息", notes = "获取采购物品供应商信息")
    @GetMapping("quote")
    public R getQuote(Long id) {
        try {
            pageUtil();
            return resultData(bizPurchaseApplyService.getQuote(id));
        } catch (Exception e) {
            logger.error("获取采购物品供应商信息失败", e);
            return R.error("查询失败");
        }
    }


}