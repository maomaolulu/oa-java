package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.asset.BizClaimApply;
import com.ruoyi.activiti.service.BizClaimApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.vo.SkuCheckVo;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 领用申请
 * @author: zx
 * @date: 2021/11/21 19:10
 * @menu 领用申请
 */
@RestController
@RequestMapping("claim_apply")
public class BizClaimApplyController extends BaseController {

    private final BizClaimApplyService claimApplyService;
    private final IBizBusinessService businessService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;

    @Autowired
    public BizClaimApplyController(BizClaimApplyService claimApplyService, IBizBusinessService businessService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService) {
        this.claimApplyService = claimApplyService;
        this.businessService = businessService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 新增领用申请
     */
    @OperLog(title = "领用申请", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增报废出库申请", notes = "")
    @PostMapping("save")
    public R addSave(@RequestBody BizClaimApply claimApply) {
        return claimApplyService.insert(claimApply);
    }

    /**
     * 查询详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    public R biz(@PathVariable("businessKey") String businessKey) {
        BizBusiness business = businessService.selectBizBusinessById(businessKey);
        if (null != business) {
            BizClaimApply claimApply = claimApplyService.selectBizClaimApplyById(business.getTableId());
            claimApply.setTitle(business.getTitle());
            return R.ok("ok", claimApply);
        }
        return R.error("no record");
    }

    /**
     * 检查库存
     *
     * @param skuCheckVos
     * @return
     */
    @PostMapping("sku")
    public R checkSku(@RequestBody List<SkuCheckVo> skuCheckVos) {
        try {
            String resultStr = claimApplyService.checkSku(skuCheckVos);
            if ("".equals(resultStr)) {
                return R.ok();
            }
            return R.error(resultStr + "库存不足");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return R.error("检查库存失败");
        }
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(claimApplyService.deleteClaimByIds(ids));
    }
}
