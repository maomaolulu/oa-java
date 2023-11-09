package com.ruoyi.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.my_apply.BizContractProject;
import com.ruoyi.activiti.service.IBizContractProjectService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author yrb
 * @Date 2023/6/13 13:12
 * @Version 1.0
 * @Description 运营系统-合同项目信息修改-审批
 */
@RestController
@RequestMapping("contract_project")
public class BizContractProjectController extends BaseController {
    private final IBizContractProjectService bizContractProjectService;

    public BizContractProjectController(IBizContractProjectService bizContractProjectService){
        this.bizContractProjectService = bizContractProjectService;
    }

    /**
     * 新增申请
     */
    @OperLog(title = "合同项目修改审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizContractProject bizContractProject) {
        try {
            if (StrUtil.isBlank(bizContractProject.getTypeNew())) {
                return R.error("项目类型不能为空");
            }
            if (StrUtil.isBlank(bizContractProject.getIdentifierNew())) {
                return R.error("项目编号不能为空");
            }
            if (StrUtil.isBlank(bizContractProject.getContractTypeNew())) {
                return R.error("合同类型不能为空");
            }
            if (StrUtil.isBlank(bizContractProject.getContractIdentifierNew())) {
                return R.error("项目编号不能为空");
            }
            if (StrUtil.isBlank(bizContractProject.getReason())){
                return R.error("修改原因不能为空");
            }
            int insert = bizContractProjectService.insert(bizContractProject);
            if (insert == -999) {
                return R.error("你的角色无法发起申请");
            }
            if (insert == 0) {
                return R.error("提交申请失败");
            }
            if (insert == -1) {
                return R.error("新的项目编号被占用");
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
     * @param id 业务类主键id
     * @return result
     */
    @GetMapping("detail/{id}")
    public R getDetail(@PathVariable("id") String id) {
        try {
            return R.data(bizContractProjectService.findDetail(id));
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }
    }

    /**
     * 获取合同项目tree
     *
     * @return result
     */
    @GetMapping("getTree")
    public R getTree() {
        try{
            return R.data(bizContractProjectService.getTree());
        }catch (Exception e){
            logger.error("获取数据失败", e);
            return R.error("获取数据失败");
        }
    }

    /**
     * 获取合同项目信息
     * @param identifier 项目编号
     * @return result
     */
    @GetMapping("/getInfo")
    public R getInfo(String identifier) {
        return R.data(bizContractProjectService.findInfo(identifier));
    }

    @GetMapping("/test")
    public R test(String identifier) {
        bizContractProjectService.test(identifier);
        return R.ok();
    }

    /**
     * 获取合同类型、项目类型
     *
     * @return result
     */
    @GetMapping("getType")
    public R getType() {
        try{
            return R.data(bizContractProjectService.getType());
        }catch (Exception e){
            logger.error("获取数据失败", e);
            return R.error("获取数据失败");
        }
    }
}
