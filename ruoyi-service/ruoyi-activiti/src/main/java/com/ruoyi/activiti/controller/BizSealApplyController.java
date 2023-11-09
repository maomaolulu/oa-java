package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.my_apply.BizSealApply;
import com.ruoyi.activiti.domain.dto.SealApplyDto;
import com.ruoyi.activiti.domain.vo.SealApplyVo;
import com.ruoyi.activiti.service.BizSealApplyService;
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
import java.util.List;
import java.util.Map;

/**
 * @author zx
 * @date 2022/1/12 20:29
 * @menu 用印申请
 */
@RestController
@RequestMapping("seal_apply")
public class BizSealApplyController extends BaseController {
    private final BizSealApplyService sealApplyService;
    private final IBizBusinessService businessService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    public BizSealApplyController(BizSealApplyService sealApplyService, IBizBusinessService businessService, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService) {
        this.sealApplyService = sealApplyService;
        this.businessService = businessService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 查询列表
     * @param sealApplyDto
     * @return
     */
    @GetMapping("list")
    public R list(SealApplyDto sealApplyDto ) {
        startPage();
        List<SealApplyVo> bizBusinesses = sealApplyService.selectList(sealApplyDto);
        return result(bizBusinesses);
    }

    /**
     * 获取用印申请编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("YZ" + today + timestamp);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "用印申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave( @RequestBody BizSealApply sealApply) {
        try {
            sealApply.setCreateTime(new Date());
            sealApply.setCreateBy(SystemUtil.getUserId().toString());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyName = belongCompany2.get("companyName").toString();

            int insert = sealApplyService.insert(sealApply);
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
            BizSealApply sealApply = sealApplyService.selectById(Long.valueOf(business.getTableId()));
            sealApply.setTitle(business.getTitle());
            return R.data(sealApply);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }
}
