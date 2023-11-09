package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.asset.BizClaim;
import com.ruoyi.activiti.domain.asset.SkuDto;
import com.ruoyi.activiti.service.BizClaimService;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领用申请2
 *
 * @author zh
 * @date 2022-1-6
 * @menu 领用申请2
 */
@RestController
@RequestMapping("biz_claim")
public class BizClaimController extends BaseController {

    private final BizClaimService bizClaimService;


    @Autowired
    public BizClaimController(BizClaimService bizClaimService) {
        this.bizClaimService = bizClaimService;
    }


    /**
     * 查询领用申请列表
     */
    @HasPermissions("biz:claim:list")
    @ApiOperation(value = "查询领用申请列表", notes = "查询领用申请列表")
    @GetMapping("list")
    public R list(BizClaim dto) {
        startPage();
        return result(bizClaimService.selectBizClaim(dto));
    }

    /**
     * 查询领用本人申请列表
     */
//	@HasPermissions("biz:claim:list")
    @ApiOperation(value = "查询领用本人申请列表", notes = "查询领用本人申请列表")
    @GetMapping("listByUser")
    public R listByUser(BizClaim dto) {
        dto.setCreateBy(SystemUtil.getUserId());
        startPage();
        return result(bizClaimService.selectBizClaim(dto));
    }


    /**
     * 新增领用申请
     */
    @ApiOperation(value = "新增领用申请", notes = "新增领用申请")
    @PostMapping("save")
    @OperLog(title = "直接领用", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody List<BizClaim> data) {
        return bizClaimService.insertBizClaim(data);
    }

    /**
     * 修改领用申请（批准）
     */
    @ApiOperation(value = "修改领用申请（批准）", notes = "修改领用申请（批准）")
    @PostMapping("update")
    @OperLog(title = "库管领用确认", businessType = BusinessType.UPDATE)
    public R update(@RequestBody List<BizClaim> bizClaims) {
        //批准领用
        for (BizClaim bizClaim : bizClaims) {
            bizClaim.setStatus("1");
        }
        return bizClaimService.update(bizClaims);
    }


    /**
     * 修改领用申请（驳回）
     */
    @ApiOperation(value = "修改领用申请（驳回）", notes = "修改领用申请（驳回）")
    @PostMapping("updateReject")
    @OperLog(title = "库管领用驳回", businessType = BusinessType.UPDATE)
    public R updateReject(@RequestBody List<BizClaim> bizClaims) {
        // 驳回领用
        for (BizClaim bizClaim : bizClaims) {
            bizClaim.setStatus("2");
        }
        return bizClaimService.update(bizClaims);
    }

    /**
     * 删除领用申请
     *
     * @param ids
     * @return
     */
//	@HasPermissions("biz:claim:remove")
    @ApiOperation(value = "删除领用申请", notes = "删除领用申请")
    @PostMapping("remove")
    public R remove(String[] ids) {
        return bizClaimService.delete(ids);
    }

    /**
     * 直接出库
     */
    @ApiOperation(value = "直接出库", notes = "直接出库")
    @PostMapping("sku_out")
    @OperLog(title = "直接出库", businessType = BusinessType.UPDATE)
    public R skuOut(@RequestBody List<SkuDto> data) {
        try {
            bizClaimService.skuOut(data);
            return R.ok();
        }catch (Exception e){
            logger.error("直接出库失败",e);
            return R.error("直接出库失败");
        }
    }
}