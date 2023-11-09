package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.dto.BizContractApplyDto;
import com.ruoyi.activiti.domain.fiance.BizContractApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizContractApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * 合同审批
 *
 * @author zh
 * @date 2021-12-24
 * @menu 合同审批
 */
@RestController
@RequestMapping("contract_apply")
public class BizContractApplyController extends BaseController {

    private final BizContractApplyService bizContractApplyService;
    private final IBizBusinessService bizBusinessService;
    @Autowired
    public BizContractApplyController(BizContractApplyService bizContractApplyService, IBizBusinessService bizBusinessService) {
        this.bizContractApplyService = bizContractApplyService;
        this.bizBusinessService = bizBusinessService;
    }


    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据businessKey查询")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        try {
            BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
             BizContractApplyDto bizContractApplyDto = bizContractApplyService.selectOne(Long.valueOf(business.getTableId()));
            bizContractApplyDto.setTitle(business.getTitle());
            return R.ok("ok",bizContractApplyDto);
        }catch (Exception e){
            System.err.println(e);
            return R.error("查询合同审批详情失败");
        }
    }

    /**
     * 查询合同审批
     */
    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "查询合同审批申请信息id", required = true)})
    @GetMapping("get/{id}")
    public R get(@PathVariable("id") Long id) {
        BizContractApplyDto dto = new BizContractApplyDto();
        dto.setId(id);
        BizContractApplyDto BizContractApplyDto = bizContractApplyService.selectBizContractApply(dto).get(0);
      return R.ok("ok",bizContractApplyService.getPurchase(BizContractApplyDto));
    }

    /**
     * 获取查询合同审批编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("HT" + today + timestamp);
    }

    /**
     * 查询合同审批列表
     */
    @ApiOperation(value = "查询合同审批列表", notes = "查询合同审批列表")
    @GetMapping("list")
    public R list(BizContractApplyDto dto) {
        startPage();
        return result(bizContractApplyService.selectBizContractApply(dto));
    }


    /**
     * 新增合同审批
     */
    @ApiOperation(value = "新增合同审批", notes = "新增合同审批")
    @OperLog(title = "新增合同审批", businessType = BusinessType.INSERT)
    @PostMapping("save")
    public R addSave(@RequestBody BizContractApply bizContractApply) {

        return bizContractApplyService.insertBizContractApply(bizContractApply);
    }


}