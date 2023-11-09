package com.ruoyi.daily.controller.asset;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.asset.dto.AssetListDto;
import com.ruoyi.daily.domain.asset.record.dto.*;
import com.ruoyi.daily.domain.asset.record.vo.BizTransferRecordVO;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferDTO;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferVO;
import com.ruoyi.daily.service.asset.AssetService;
import com.ruoyi.daily.service.asset.record.BizTransferRecordService;
import com.ruoyi.daily.service.asset.transfer.AssetTransferService;
import com.ruoyi.daily.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产转移接口
 * Created by WuYang on 2022/8/23 9:16
 */
@Api(tags = {"资产转移接口"})
@RestController
@RequestMapping("transfer")
public class AssetTransferController extends BaseController {

    @Resource
    AssetService assetService;


    @Resource
    BizTransferRecordService bizTransferRecordService;

    @Resource
    AssetTransferService assetTransferService;
    /**
     * 查询列表
     * @param assetListDto
     * @return
     */
    @ApiOperation("查询列表")
    @GetMapping("list")
    public R list(AssetListDto assetListDto){
        try {
            startPage();
            return result(assetService.getList(assetListDto));
        }catch (Exception e){
            logger.error("获取固定资产列表",e);
            return R.error("获取固定资产列表失败");
        }
    }

    /**
     * 资产转移给其他人
     */
    @ApiOperation(value = "资产转移")
    @OperLog(title = "资产转移", businessType = BusinessType.UPDATE)
    @PostMapping("/toOther")
    public R assetTransfer(@RequestBody @Validated AssetListsDTO dto) {
        try {
            List<AssetDTO> list = dto.getList();
            list.forEach(bizTransferRecordService::assetTransfer);
            return R.ok("资产转移成功");
        } catch (Exception e) {
            logger.error("资产转移失败:{}", e.getMessage());
            return R.error("资产转移失败");
        }

    }


    /**
     * 资产设为库存
     */
    @ApiOperation(value = "资产设为库存")
    @OperLog(title = "资产设为库存", businessType = BusinessType.UPDATE)
    @PostMapping("/toStock")
    public R setStock(@RequestBody @Validated AssetStockListDTO dto) {
        try {
            List<AssetStockDTO> list = dto.getList();
            list.forEach(bizTransferRecordService::toStock);
            return R.ok("资产设为库存成功");
        } catch (Exception e) {
            logger.error("资产设为库存失败:{}", e.getMessage());
            return R.error("资产设为库存失败");
        }
    }

    /**
     * 查询记录
     */
    @ApiOperation(value = "查询移交记录")
    @GetMapping("/getList")
    public R getAssetRecordList(AssetSelectDTO dto) {
        try {
            String start = dto.getStart();
            if (StringUtils.isNotBlank(start)) {
                LocalDateTime startTime = DateUtil.parse(start).toLocalDateTime();
                dto.setStartTime(startTime);
            }
            String end = dto.getEnd();
            if (StringUtils.isNotBlank(end)) {
                LocalDateTime endTime = DateUtil.parse(end).toLocalDateTime();
                dto.setEndTime(endTime);
            }
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
            List<BizTransferRecordVO> list = bizTransferRecordService.getList(dto);
            return R.data(new PageInfo<BizTransferRecordVO>(list));
        } catch (Exception e) {
            logger.error("查询记录失败{}",e.getMessage());
            return R.error("查询记录失败");
        }
    }

    /**
     * 采购资产转移
     */
    @ApiOperation(value = "采购资产转移")
    @OperLog(title = "采购资产转移", businessType = BusinessType.UPDATE)
    @PostMapping("/purchaseTransfer")
    public Result<?> purchaseTransfer(@RequestBody @Validated PurchaseListDTO dto) {
        try {
            List<PurchaseTransferDTO> list = dto.getList();
            list.forEach(assetTransferService::purchaseTransfer);
            return Result.ok("采购资产转移成功");
        } catch (Exception e) {
            logger.error("采购资产转移失败:{}", e.getMessage());
            return Result.error("采购资产转移失败");
        }
    }
    /**
     * 用户确认状态
     */
    @ApiOperation(value = "用户确认状态")
    @OperLog(title = "用户确认状态", businessType = BusinessType.UPDATE)
    @PostMapping("/checkPurchase")
    public Result<?> checkPurchase(@RequestBody @Validated PurchaseCheckListDTO dto) {
        try {
            List<Long> list = dto.getList();
            list.forEach(assetTransferService::checkPurchase);
            return Result.ok("用户确认状态成功");
        } catch (Exception e) {
            logger.error("用户确认状态失败:{}", e.getMessage());
            return Result.error("用户确认状态失败");
        }
    }
    /**
     * 用户驳回状态
     */
    @ApiOperation(value = "用户驳回状态")
    @OperLog(title = "用户驳回状态", businessType = BusinessType.UPDATE)
    @PostMapping("/uncheckPurchase")
    public Result<?> uncheckPurchase(@RequestBody @Validated PurchaseCheckListDTO dto) {
        try {
            List<Long> list = dto.getList();
            list.forEach(assetTransferService::rejectCheck);
            return Result.ok("用户驳回状态成功");
        } catch (Exception e) {
            logger.error("用户驳回状态失败:{}", e.getMessage());
            return Result.error("用户驳回状态失败");
        }
    }
    /**
     * 展示用户为确认的转移待确认记录
     */
    @ApiOperation(value = "转移待确认记录")
    @PostMapping("/getUncheckPurchaseListByUser")
    public R getUncheckPurchaseListByUser(
            @RequestParam(value = "purchaseCode",required = false) String purchaseCode,
            @RequestParam(value = "createBy",required = false) String createBy,
            @RequestParam(value = "assetSn" ,required = false) String assetSn,
            @RequestParam(value = "name",required = false)String name,
            @RequestParam(value = "start",required = false) String start,
            @RequestParam(value = "end",required = false) String end


            )
     {
        try {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (StringUtils.isNotBlank(start)) {
                startDateTime = DateUtil.parseDate(start).toLocalDateTime();
            }
            if (StringUtils.isNotBlank(end)) {
                endDateTime = DateUtil.parseDate(end).toLocalDateTime();
            }
            pageUtil();
            List<PurchaseTransferVO> uncheckPurchaseListByUser = assetTransferService.getUncheckPurchaseListByUser(purchaseCode,createBy,assetSn,name,startDateTime,endDateTime);
            return resultData(uncheckPurchaseListByUser);
        } catch (Exception e) {
            logger.error("转移待确认记录失败:{}", e.getMessage());
            return R.error("转移待确认记录失败");
        }
    }
    /**
     * 查询列表
     * @return
     */
    @ApiOperation("修补")
    @GetMapping("repair")
    public R repair(){
        try {
          bizTransferRecordService.repairAsset();
          return R.ok();
        }catch (Exception e){
            logger.error("获取固定资产列表",e);
            return R.error("获取固定资产列表失败");
        }
    }
}
