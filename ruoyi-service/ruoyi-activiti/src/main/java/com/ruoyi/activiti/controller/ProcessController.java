package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 流程管理
 *
 * @menu 流程管理
 */
@Slf4j
@RestController
@RequestMapping("process")
@Api("流程管理")
public class ProcessController {
    private final ProcessService processService;

    @Autowired
    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }


    @RequestMapping("deployByFile")
    @OperLog(title = "流程部署", businessType = BusinessType.INSERT)
    public R upload(MultipartFile file) {
        return processService.upload(file);
    }

    /**
     * 查看流程图
     */
    @GetMapping("show")
    public void show(@RequestParam("did") String did, @RequestParam("ext") String ext,
                     HttpServletResponse httpServletResponse) throws IOException {
        processService.show(did, ext, httpServletResponse);
    }

    /**
     * 挂起、激活流程实例
     */
    @RequestMapping(value = "update/{processId}/{state}")
    @OperLog(title = "挂起激活流程实例", businessType = BusinessType.UPDATE)
    public R updateState(@PathVariable("state") String state, @PathVariable("processId") String processId) {
        return processService.updateState(state, processId);
    }

    /**
     * proc 实时高亮流程图 
     *
     * @param procInstId 流程实例ID
     * @param response
     * @author zmr
     */
    @RequestMapping(value = "highlightImg/{procInstId}")
    public void getHighlightImg(@PathVariable String procInstId, HttpServletResponse response) {
        processService.getHighlightImg(procInstId, response);
    }



    /**
     * 获取完整流程列表
     */
    @ApiOperation(value = "获取完整流程列表", notes = "获取完整流程列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "流程编码例如:leave,purchase", required = true),
            @ApiImplicitParam(name = "tableId", value = "我的待办我的申请传业务表id,提交申请时传空值", required = false)})
    @GetMapping("/process_all")
    @OperLog(title = "获取完整流程列表", businessType = BusinessType.OTHER)
    public R getProcessAll(String name,@RequestParam(value = "tableId", required = false) String tableId, Long userId,@RequestParam(value = "procInstId", required = false) String procInstId, @RequestParam(value = "money", required = false) BigDecimal money,@RequestParam(value = "variable", required = false)String variable) {
        log.info("userid----------" + userId + "-----------name---------" + name + "---------tableId-------" + tableId);
        userId = SystemUtil.getUserId();
        log.info("userid2----------" + userId + "-----------name---------" + name + "---------tableId-------" + tableId);
        return R.ok("获取完整流程列表成功",  processService.getProcessAll(name, tableId, userId, money,variable,procInstId));
    }
}
