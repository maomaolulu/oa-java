package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.my_apply.BizUniversalApply;
import com.ruoyi.activiti.service.BizUniversalApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author zx
 * @date 2022/1/14 17:21
 * @menu 通用审批
 */
@RestController
@RequestMapping("universal_apply")
public class BizUniversalApplyController extends BaseController {
    private final BizUniversalApplyService universalApplyService;
    private final IBizBusinessService businessService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;

    @Autowired
    public BizUniversalApplyController(BizUniversalApplyService universalApplyService, IBizBusinessService businessService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService) {
        this.universalApplyService = universalApplyService;
        this.businessService = businessService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 获取通用审批编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("TY" + today + timestamp);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "通用审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizUniversalApply universalApply) {
        try {
            universalApply.setCreateTime(new Date());
            universalApply.setCreateBy(SystemUtil.getUserId().toString());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());

            int insert = universalApplyService.insert(universalApply);
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
            BizUniversalApply universalApply = universalApplyService.selectById(Long.valueOf(business.getTableId()));
            universalApply.setTitle(business.getTitle());
            return R.data(universalApply);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }
}
