package com.ruoyi.activiti.controller.my_apply;

import com.ruoyi.activiti.domain.my_apply.BizDemandFeedback;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.my_apply.BizDemandFeedbackService;
import com.ruoyi.activiti.utils.CodeUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 需求反馈
 *
 * @author zx
 * @date 2022/3/21 11:13
 * @menu 需求反馈
 */
@RestController
@RequestMapping("feedback")
public class BizDemandFeedbackController extends BaseController {
    private final IBizBusinessService businessService;
    private final BizDemandFeedbackService demandFeedbackService;

    @Autowired
    public BizDemandFeedbackController(IBizBusinessService businessService, BizDemandFeedbackService demandFeedbackService) {
        this.businessService = businessService;
        this.demandFeedbackService = demandFeedbackService;
    }


    /**
     * 获取需求反馈审批编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        return R.data(CodeUtil.getCode("XQ"));
    }

    /**
     * 新增申请
     */
    @OperLog(title = "需求反馈审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizDemandFeedback demandFeedback) {
        try {
            demandFeedback.setCreateTime(new Date());
            demandFeedback.setCreateBy(SystemUtil.getUserId().toString());

            int insert = demandFeedbackService.insert(demandFeedback);
            if (insert == 0) {
                return R.error("提交申请失败");
            }
            return R.ok("提交申请成功");
        } catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }

    }

    /**
     * 获取详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            if (null == business) {
                return R.error("获取流程信息失败");
            }
            BizDemandFeedback demandFeedback = demandFeedbackService.selectById(Long.valueOf(business.getTableId()));
            demandFeedback.setTitle(business.getTitle());
            return R.data(demandFeedback);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }
}
