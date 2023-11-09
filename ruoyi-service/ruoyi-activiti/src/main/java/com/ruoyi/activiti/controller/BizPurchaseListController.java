package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.dto.AssetPurchaseDto;
import com.ruoyi.activiti.domain.dto.BizPurchaseListDto;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizTransferRecord;
import com.ruoyi.activiti.mapper.BizGoodsInfoMapper;
import com.ruoyi.activiti.service.BizPurchaseListService;
import com.ruoyi.activiti.utils.FileUtil;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.exception.RuoyiException;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.EasyExcelUtil;
import com.ruoyi.common.utils.NoModelWriteData;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 采购订货-验收-查询
 *
 * @author zh
 * @date 2021-12-13
 * @menu 采购订货-验收-查询
 */

@RestController
@RequestMapping("purchase_list")
public class BizPurchaseListController extends BaseController {

    private final BizPurchaseListService bizPurchaseListService;
    @Autowired
    private BizGoodsInfoMapper goodsInfoMapper;

    @Autowired
    public BizPurchaseListController( BizPurchaseListService bizPurchaseListService) {
        this.bizPurchaseListService = bizPurchaseListService;
    }

    /**
     * 固定资产采购申请页面
     *
     * @param dto
     * @return
     */
//    @HasPermissions("purchase:list:goodsAll")
    @GetMapping("selectGoodsListFixed")
    public R selectGoodsListFixed(BizPurchaseListDto dto) {
        startPage();
        return result(bizPurchaseListService.selectGoodsListFixed(dto));
    }

    /**
     * 所有采购订单查询(采购查询页面)
     *
     * @param dto
     * @return
     */
//    @HasPermissions("purchase:list:goodsAll")
    @GetMapping("selectGoodsListAll")
    public R selectGoodsListAll(BizPurchaseListDto dto) {
        startPage();
        return result(bizPurchaseListService.selectGoodsListAll(dto));
    }

    /**
     * 采购订货和采购计划（页面）
     *
     * @param dto
     * @return
     */
    @HasPermissions("purchase:list:info")
    @GetMapping("selectBizPurchaseList")
    public R selectBizPurchaseList(BizPurchaseListDto dto) {
        startPage();
        List<BizPurchaseListDto> bizPurchaseListDtos = bizPurchaseListService.selectBizPurchaseListAll(dto);
        return result(bizPurchaseListDtos);
    }

    @OperLog(title = "导出", businessType = BusinessType.EXPORT)
    @HasPermissions("purchase:list:info")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody BizPurchaseListDto dto) throws IOException {
        dto.setExport("导出");
        List<BizPurchaseListDto> bizPurchaseListDtos = bizPurchaseListService.selectBizPurchaseListAll(dto);
        ExcelUtil<BizPurchaseListDto> util = new ExcelUtil<>(BizPurchaseListDto.class);
        String fileName = FileUtil.getDateTimeFormat(3);
        util.exportExcelWithFilename(response, bizPurchaseListDtos, "待采购清单", fileName);
    }

    /**
     * 采购验收页面
     *
     * @param dto
     * @return
     */
    @HasPermissions("purchase:list:check")
    @GetMapping("selectBizPurchaseCheck")
    public R selectBizPurchaseCheck(BizPurchaseListDto dto) {
        startPage();
        return result(bizPurchaseListService.selectBizPurchaseCheck(dto));
    }

    /**
     * 采购入库页面
     *
     * @param dto
     * @return
     */
    @HasPermissions("purchase:Warehousing:view")
    @GetMapping("selectBizPurchaseApplyList")
    public R selectBizPurchaseApplyList(BizPurchaseListDto dto) {
        startPage();
        return result(bizPurchaseListService.selectBizPurchaseApplyList(dto));
    }

    /**
     * 去采购（修改）
     *
     * @param
     * @returnsssxz
     */
    @HasPermissions("purchase:list:update")
    @OperLog(title = "去采购", businessType = BusinessType.UPDATE)
    @PostMapping("updateBizGoodsInfo")
    public R updateBizGoodsInfo(@RequestBody BizGoodsInfo bizGoodsInfos) {
        return R.ok("ok", bizPurchaseListService.updateBizGoodsInfo(bizGoodsInfos));
    }

    /**
     * 采购人员申请验收（修改）
     *
     * @param bizGoodsInfoList
     * @return
     */
    @HasPermissions("purchase:list:update")
    @OperLog(title = "采购人员申请验收（修改）", businessType = BusinessType.UPDATE)
    @PostMapping("updateBizGoodsInfoList")
    public R updateBizGoodsInfoList(@RequestBody List<BizGoodsInfo> bizGoodsInfoList) {
        bizPurchaseListService.updateBizGoodsInfoList(bizGoodsInfoList);
        return R.ok();
    }

    /**
     * 采购计划修改
     *
     * @param bizGoodsInfo
     * @return
     */
    @HasPermissions("purchase:list:update")
    @OperLog(title = "采购计划修改（修改）", businessType = BusinessType.UPDATE)
    @PostMapping("updateBizGoodsInfoListPlan")
    public R updateBizGoodsInfoListPlan(@RequestBody BizGoodsInfo bizGoodsInfo) {
        bizPurchaseListService.updateBizGoodsInfoPlan(bizGoodsInfo);
        return R.ok();
    }

    /**
     * 申请人验收（修改）
     *
     * @param
     * @return
     */
    @HasPermissions("purchase:list:update2")
    @OperLog(title = "申请人验收（修改）", businessType = BusinessType.UPDATE)
    @PostMapping("updateBizGoodsInfoList2")
    public R updateBizGoodsInfoList2(@RequestBody List<BizGoodsInfo> bizGoodsInfoList) {
        bizPurchaseListService.updateBizGoodsInfoList2(bizGoodsInfoList);
        return R.ok();
    }

    /**
     * 采购入库（修改入库字段）
     *
     * @param
     * @return
     */
    @HasPermissions("purchase:list:warehousing")
    @OperLog(title = "采购入库（修改入库字段）", businessType = BusinessType.UPDATE)
    @PostMapping("updateBizGoodsInfoWarehousing")
    public R updateBizGoodsInfoWarehousing(@RequestBody List<BizGoodsInfo> bizGoodsInfoList) {
        for (BizGoodsInfo bizGoodsInfo : bizGoodsInfoList) {
            //待入库数量
            Integer warehousingAmount = bizGoodsInfo.getWarehousingAmount();
            //入库数量
            Integer actualWarehousingAmount = bizGoodsInfo.getActualWarehousingAmount();
            if (warehousingAmount - actualWarehousingAmount < 0) throw new RuoyiException("入库数量不能大于待入库数量");
            bizGoodsInfo.setWarehousingAmount(warehousingAmount - actualWarehousingAmount);
            if (warehousingAmount - actualWarehousingAmount == 0) {
                bizGoodsInfo.setIsStorage("1");
            } else {
                bizGoodsInfo.setIsStorage("0");
            }
        }
        bizPurchaseListService.updateBizGoodsInfoList(bizGoodsInfoList);
        return R.ok();
    }

    /**
     * 资产入库（未移交）
     *
     * @param
     * @return
     */
    @OperLog(title = "资产入库（未移交）", businessType = BusinessType.INSERT)
    @PostMapping("storage_no_transfer")
    public R storageNoTransfer(Long id) {
        try {
            bizPurchaseListService.storageNoTransfer(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("资产入库（未移交）", e);
            return R.error("保存采购记录失败");
        }
    }

    /**
     * 查询采购流程
     *
     * @param id 采购物品id goods_id
     * @return
     */
    @OperLog(title = "查询采购流程", businessType = BusinessType.INSERT)
    @GetMapping("goods_record")
    public R getGoodsRecord(Long id) {
        try {
            return resultData(bizPurchaseListService.getGoodsRecord(id));
        } catch (Exception e) {
            logger.error("查询采购流程", e);
            return R.error("查询采购流程失败");
        }
    }

    /**
     * 固定资产移交
     *
     * @param
     * @return
     */
    @OperLog(title = "固定资产移交", businessType = BusinessType.INSERT)
    @PostMapping("storage_transfer")
    public R storageTransfer(@RequestBody List<BizTransferRecord> transferRecords) {
        try {
            bizPurchaseListService.storageTransfer(transferRecords);
            return R.ok("移交成功");
        } catch (Exception e) {
            logger.error("固定资产移交", e);
            return R.error("固定资产移交失败");
        }
    }

    /**
     * 查询移交记录
     *
     * @param bizTransferRecord
     * @return
     */
    @OperLog(title = "查询移交记录", businessType = BusinessType.INSERT)
    @GetMapping("transfer_record")
    public R getTransferRecord(BizTransferRecord bizTransferRecord) {
        try {
            pageUtil();
            return resultData(bizPurchaseListService.getTransferRecord(bizTransferRecord));
        } catch (Exception e) {
            logger.error("查询移交记录", e);
            return R.error("查询移交记录失败");
        }
    }

    /**
     * 采购物品作废
     *
     * @param
     * @return
     */
    @OperLog(title = "采购物品作废", businessType = BusinessType.DELETE)
    @DeleteMapping("goods/{id}")
    public R cancelGoods(@PathVariable("id") Long id) {
        try {
            bizPurchaseListService.cancelGoods(id);
            return R.ok("操作成功");
        } catch (Exception e) {
            logger.error("采购物品作废", e);
            return R.error("操作失败");
        }
    }

    /**
     * 入库前退货
     * @param ids 采购物品id数组
     * @return
     */
    @OperLog(title = "入库前退货", businessType = BusinessType.UPDATE)
    @PutMapping("goods/return")
    public R  beforeStorageReturn(@RequestBody List<Long> ids) {
        try {
            bizPurchaseListService.beforeStorageReturn(ids);
            return R.ok("操作成功");
        } catch (Exception e) {
            logger.error("入库前退货失败", e);
            return R.error("操作失败");
        }
    }
    /**
     * 确认移交
     * @param ids 移交记录ids
     * @return
     */
    @OperLog(title = "确认移交", businessType = BusinessType.UPDATE)
    @PutMapping("check_transfer")
    public R  checkTransfer(@RequestBody List<Long> ids) {
        try {
            bizPurchaseListService.checkTransfer(ids);
            return R.ok("操作成功");
        } catch (Exception e) {
            logger.error("确认移交失败", e);
            return R.error("操作失败");
        }
    }

    /**
     * 设为库存
     * @param assetIds 固定资产id
     * @return
     */
    @OperLog(title = "设为库存", businessType = BusinessType.UPDATE)
    @PutMapping("keep_storage")
    public R  setStorage(@RequestBody List<Long> assetIds) {
        try {
            bizPurchaseListService.setStorage(assetIds);
            return R.ok("操作成功");
        } catch (Exception e) {
            logger.error("设为库存失败", e);
            return R.error("操作失败");
        }
    }

    /**
     * 资产采购导出列表
     * @param assetPurchaseDto
     * @return
     */
    @GetMapping("excel")
    public void exportAssetPurchaseExcel(HttpServletResponse response, AssetPurchaseDto assetPurchaseDto){
        try {
            NoModelWriteData noModelWriteData = new NoModelWriteData();

            List<Map<String, Object>> list = bizPurchaseListService.exportAssetPurchaseExcel(assetPurchaseDto);
            noModelWriteData.setDataList(list);

            noModelWriteData.setFileName("资产采购数据");
            String[] headMap = {"申请编号","申请部门","状态","申请时间","费用归属部门","申请人","备注","采购类型","物品名称","规格型号","单位","数量","物品备注","供应商报价均价"};
            String[] dataStrMap={"apply_code","dept_name","status","apply_time","belong_dept_name","applyer","apply_remark","dict_label","name","model","unit","amount","remark","average_quote"};
            noModelWriteData.setHeadMap(headMap);
            noModelWriteData.setDataStrMap(dataStrMap);
            EasyExcelUtil.noModuleWriteByMap(noModelWriteData,response);

        }catch (Exception e){
            logger.error("导出固定资产列表",e);
        }
    }

    @GetMapping("test")
    public R getTransferRecord() {
        try {
            pageUtil();
            Example example = new Example(BizGoodsInfo.class);
            example.selectProperties("id", "goodType").and().andEqualTo("purchaseId", 340);
            List<BizGoodsInfo> bizGoodsInfos = goodsInfoMapper.selectByExample(example);
            return resultData(bizGoodsInfos);
        } catch (Exception e) {
            logger.error("查询移交记录", e);
            return R.error("查询移交记录失败");
        }
    }

}
