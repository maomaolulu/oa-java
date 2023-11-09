package com.ruoyi.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.activiti.domain.my_apply.BizContractInfo;
import com.ruoyi.activiti.domain.my_apply.BizContractProjectInfo;
import com.ruoyi.activiti.domain.my_apply.BizContractReview;
import com.ruoyi.activiti.service.BizContractReviewService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/5/22 13:31
 * @Version 1.0
 * @Description 合同评审
 */
@RestController
@RequestMapping("contract_review")
public class BizContractReviewController extends BaseController {
    private final BizContractReviewService bizContractReviewService;

    public BizContractReviewController(BizContractReviewService bizContractReviewService) {
        this.bizContractReviewService = bizContractReviewService;
    }

    /**
     * 新增申请
     */
    @OperLog(title = "合同评审", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizContractReview bizContractReview) {
        try {

            // 合同信息
            BizContractInfo bizContractInfo = bizContractReview.getBizContractInfo();
            Long id = bizContractInfo.getId();
            // 项目信息
            List<BizContractProjectInfo> projectInfoList = bizContractReview.getBizContractProjectInfoList();
            if (id == null) {
                return R.error("合同ID不能为空");
            }
            if (CollUtil.isEmpty(projectInfoList)) {
                return R.error("项目信息不能为空");
            }
            if (bizContractReviewService.isProcessing(id)) {
                return R.error("合同" + id + "处于审批状态中，请等待处理完成！");
            }
            int insert = bizContractReviewService.insert(bizContractReview);
            if (insert == -2) {
                return R.error("合同信息保存失败");
            }
            return R.ok("合同审批数据推送成功,请等待审核！");
        } catch (Exception e) {
            logger.error("===================================推送合同审批信息发生异常===========================" + e);
            return R.error("系统异常，请联系管理员");
        }
    }

    /**
     * 获取详情
     *
     * @param tableId 合同信息表主键id
     * @return result
     */
    @GetMapping("detail/{tableId}")
    public R getDetail(@PathVariable("tableId") String tableId) {
        try {
            return R.data(bizContractReviewService.findDetail(tableId));
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }
    }

    /**
     * 撤回流程
     *
     * @param bizContractInfo 合同ID
     * @return result
     */
    @PostMapping("/revokeProcess")
    public R revokeProcess(@RequestBody BizContractInfo bizContractInfo) {
        try {
            Long contractId = bizContractInfo.getContractId();
            if (contractId == null) {
                return R.error("合同ID不能为空");
            }
            if (bizContractReviewService.revokeProcess(contractId)) {
                return R.ok("流程撤销成功");
            }
            return R.error("流程撤销失败");
        } catch (Exception e) {
            logger.error("发生异常========" + e);
            return R.error("未知异常，请联系管理员");
        }
    }
}
