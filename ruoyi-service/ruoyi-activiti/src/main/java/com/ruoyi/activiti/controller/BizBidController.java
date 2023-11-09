package com.ruoyi.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.my_apply.BizBidApply;
import com.ruoyi.activiti.service.BizBidService;
import com.ruoyi.activiti.utils.CodeUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author yrb
 * @Date 2023/5/31 15:06
 * @Version 1.0
 * @Description 投标审批
 */
@RestController
@RequestMapping("bid")
public class BizBidController extends BaseController {
    private final BizBidService bizBidService;

    public BizBidController(BizBidService bizBidService) {
        this.bizBidService = bizBidService;
    }

    /**
     * 获取招标审批申请编号
     */
    @GetMapping("code")
    public R getCode() {
        return R.data(CodeUtil.getCode("TB"));
    }

    /**
     * 新增申请
     */
    @OperLog(title = "招标审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizBidApply bizBidApply) {
        try {
            if (StrUtil.isBlank(bizBidApply.getApplyCode())) {
                return R.error("审批编号不能为空");
            }
            int insert = bizBidService.insert(bizBidApply);
            if (insert == 0) {
                return R.error("招标申请提交失败");
            }
            return R.ok("招标申请提交成功");
        } catch (Exception exception) {
            logger.error("提交招标申请发生异常=============" + exception);
            return R.error("未知异常，请联系管理员！");
        }
    }

    /**
     * 获取详情
     *
     * @param tableId 招标主键id
     * @return result
     */
    @GetMapping("detail/{tableId}")
    public R getDetail(@PathVariable("tableId") String tableId) {
        try {
            return R.data(bizBidService.findDetail(tableId));
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }
    }
}
