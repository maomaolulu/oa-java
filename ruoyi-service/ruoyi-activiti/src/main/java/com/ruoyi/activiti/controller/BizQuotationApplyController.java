package com.ruoyi.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.dto.BizQuotationApplyDTO;
import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;
import com.ruoyi.activiti.service.BizQuotationApplyService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.web.bind.annotation.*;


/**
 * @Author yrb
 * @Date 2023/5/4 16:00
 * @Version 1.0
 * @Description 报价申请
 */
@RestController
@RequestMapping("quotation_apply")
public class BizQuotationApplyController extends BaseController {
    private final BizQuotationApplyService bizQuotationApplyService;
    private final RemoteUserService remoteUserService;
    private final RemoteConfigService remoteConfigService;

    public BizQuotationApplyController(BizQuotationApplyService bizQuotationApplyService,
                                       RemoteUserService remoteUserService,
                                       RemoteConfigService remoteConfigService) {
        this.bizQuotationApplyService = bizQuotationApplyService;
        this.remoteUserService = remoteUserService;
        this.remoteConfigService = remoteConfigService;
    }

    /**
     * 新增申请
     */
    @OperLog(title = "报价审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizQuotationApplyDTO bizQuotationApplyDTO) {
        try {

            BizQuotationApply bizQuotationApply = bizQuotationApplyDTO.getBizQuotationApply();
            String code = bizQuotationApply.getCode();
            if (StrUtil.isBlank(code)) {
                return R.error("报价单ID不能为空");
            }
            if (bizQuotationApplyService.isProcessing(code)) {
                return R.error("当前报价申请正在审批中");
            }
            int insert = bizQuotationApplyService.insert(bizQuotationApplyDTO);
            if (insert == -2) {
                return R.error("保存报价申请数据失败");
            }
            return R.ok("报价审批数据推送成功");
        } catch (Exception e) {
            logger.error("===================================推送报价审批发生异常===========================" + e);
            return R.error("系统异常，请联系管理员");
        }
    }

    /**
     * 获取详情
     *
     * @param tableId 报价信息表主键id
     * @return result
     */
    @GetMapping("detail/{tableId}")
    public R getDetail(@PathVariable("tableId") String tableId) {
        try {
            return R.data(bizQuotationApplyService.findDetail(tableId));
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }
    }

    /**
     * 获取用户信息
     *
     * @param email 邮箱
     * @return result
     */
    @GetMapping("info")
    public R getInfo(@RequestParam("email") String email) {
        try {
            SysUser sysUser = remoteUserService.selectSysUserByEmail(email);
            if (sysUser != null && sysUser.getUserId() != null) {
                sysUser.setPassword(null);
                return R.data(sysUser);
            }
            return R.error("OA系统不存在当前用户");
        } catch (Exception e) {
            logger.error("==========获取用户信息失败==========", e);
            return R.error("获取详情失败");
        }
    }

    /**
     * 撤回
     *
     * @param bizQuotationApply 报价单编号
     * @return result
     */
    @PostMapping("/revokeProcess")
    public R revokeProcess(@RequestBody BizQuotationApply bizQuotationApply) {
        try {
            String code = bizQuotationApply.getCode();
            if (StrUtil.isBlank(code)) {
                return R.error("报价单编号不能为空");
            }
            if (bizQuotationApplyService.revokeProcess(code)) {
                return R.ok("撤销成功");
            } else {
                return R.error("报价单" + code + "未在审核中");
            }
        } catch (Exception e) {
            logger.error("==========撤销流程失败==========", e);
            return R.error("撤销失败");
        }
    }
}
