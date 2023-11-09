package com.ruoyi.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.dto.BizApplyLimitDTO;
import com.ruoyi.activiti.service.BizApplyLimitService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

/**
 * @Author yrb
 * @Date 2023/4/19 15:28
 * @Version 1.0
 * @Description 财务申请部门限制及角色限制
 */
@RestController
@RequestMapping("limit")
public class BizApplyLimitController extends BaseController {
    private final BizApplyLimitService bizApplyLimitService;

    public BizApplyLimitController(BizApplyLimitService bizApplyLimitService) {
        this.bizApplyLimitService = bizApplyLimitService;
    }

    @PostMapping("/insertLimitInfo")
    public R insertLimitInfo(@RequestBody BizApplyLimitDTO bizApplyLimitDTO) {
        try {
            if (CollUtil.isEmpty(bizApplyLimitDTO.getProcessKeyList())) {
                return R.error("processKey不能为空");
            }
            bizApplyLimitService.insertLimitInfo(bizApplyLimitDTO);
            return R.ok("设置成功");
        } catch (Exception e) {
            logger.error("设置限制部门和角色失败", e);
            return R.error("设置失败");
        }
    }

    @GetMapping("findLimitInfo")
    public R findLimitInfo(String processKey) {
        try {
            if (StrUtil.isBlank(processKey)){
                return R.error("费用类型唯一标识processKey不能为空");
            }
            return R.data(bizApplyLimitService.findLimitInfo(processKey));
        } catch (Exception e) {
            logger.error("查询列表时发生异常", e);
            return R.error("查询列表时发生异常");
        }
    }
}
