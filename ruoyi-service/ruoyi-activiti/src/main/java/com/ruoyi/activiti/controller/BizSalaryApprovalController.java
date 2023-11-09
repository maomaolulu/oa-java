package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.my_apply.BizSalaryApproval;
import com.ruoyi.activiti.service.BizSalaryApprovalService;
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
import java.util.Map;

/**
 * @author zx
 * @date 2022/3/8 22:07
 * @menu 薪资核准
 */
@RestController
@RequestMapping("salary_approval")
public class BizSalaryApprovalController extends BaseController {
    private final BizSalaryApprovalService salaryApprovalService;
    private final IBizBusinessService businessService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    public BizSalaryApprovalController(BizSalaryApprovalService salaryApprovalService, IBizBusinessService businessService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService) {
        this.salaryApprovalService = salaryApprovalService;
        this.businessService = businessService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 获取薪资核准申请编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("XZHZ" + today + timestamp);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "薪资核准申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave( @RequestBody BizSalaryApproval salaryApproval) {
        try {
            salaryApproval.setCreateTime(new Date());
            salaryApproval.setCreateBy(SystemUtil.getUserId().toString());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyName = belongCompany2.get("companyName").toString();

            int insert = salaryApprovalService.insert(salaryApproval);
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
            BizSalaryApproval salaryApproval = salaryApprovalService.selectById(Long.valueOf(business.getTableId()));
            salaryApproval.setTitle(business.getTitle());
            return R.data(salaryApproval);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }
}
