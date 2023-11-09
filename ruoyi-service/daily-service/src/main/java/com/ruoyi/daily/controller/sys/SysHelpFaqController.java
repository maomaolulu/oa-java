package com.ruoyi.daily.controller.sys;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.sys.SysHelpHot;
import com.ruoyi.daily.domain.sys.SysHelpVersion;
import com.ruoyi.daily.domain.sys.dto.*;
import com.ruoyi.daily.domain.sys.dto.group.Insert;
import com.ruoyi.daily.domain.sys.dto.group.Update;
import com.ruoyi.daily.domain.sys.vo.SysHelpFaqVO;
import com.ruoyi.daily.service.sys.SysHelpFaqInfoService;
import com.ruoyi.daily.service.sys.SysHelpFaqService;
import com.ruoyi.daily.service.sys.SysHelpVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 帮助中心管理
 * Created by WuYang on 2022/8/18 15:08
 */
@Api(tags = {"帮助中心"})
@RestController
@RequestMapping("daily")
public class SysHelpFaqController extends BaseController {
    @Resource
    SysHelpFaqService faqService;
    @Resource
    SysHelpFaqInfoService infoService;

    @ApiOperation("获取帮助中心列表")
    @GetMapping("fag/list")
    public R getList() {
        try {
            List<SysHelpFaqVO> lists = faqService.getLists();
            return R.data(lists);
        } catch (Exception e) {
            logger.error("获取帮助中心失败",e);
            return R.error("获取帮助中心失败");
        }
    }
    /**
     * 新增帮助中心
     */
    @ApiOperation("新增帮助中心")
    @PostMapping("fag")
    @OperLog(title = "新增帮助中心", businessType = BusinessType.INSERT)
    public R add(@RequestBody SysHelpFaqAddList dto) {
        try {
           faqService.add(dto);
            return R.ok();
        } catch (Exception e) {
            logger.error("新增帮助中心失败", e);
            return R.error("新增帮助中心失败");
        }

    }

//    /**
//     * 编辑新增帮助中心
//     */
//    @ApiOperation("编辑新增帮助中心")
//    @PostMapping("edit/fag")
//    @OperLog(title = "新增帮助中心", businessType = BusinessType.INSERT)
//    public R editAdd(@RequestBody SysHelpFaqInfoDTO infoDTO) {
//        try {
//            infoService.editAdd(infoDTO);
//            return R.ok();
//        } catch (Exception e) {
//            logger.error("编辑新增帮助中心失败", e);
//            return R.error("编辑新增帮助中心失败");
//        }
//
//    }

    /**
     * 删除帮助中心
     */
    @ApiOperation("删除帮助中心")
    @DeleteMapping("fag")
    @OperLog(title = "删除帮助中心", businessType = BusinessType.DELETE)
    public R delete(@RequestParam("id") Long id) {
        try {
            faqService.delete(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("删除帮助中心失败", e);
            return R.error("删除帮助中心失败");
        }
    }
    /**
     * 更新帮助中心
     */
    @ApiOperation("更新帮助中心")
    @PutMapping("fag")
    @OperLog(title = "更新帮助中心", businessType = BusinessType.UPDATE)
    public R update(@RequestBody SysHelpFaqUpdateDTO updateDTO) {
        try {
            faqService.update(updateDTO);
            return R.ok();
        } catch (Exception e) {
            logger.error("更新帮助中心失败", e);
            return R.error("更新帮助中心失败");
        }
    }

    /**
     * 查询一个帮助中心条目
     */
    @ApiOperation("查询一个帮助中心条目")
    @GetMapping("faq")
    public R select(@RequestParam("id") Long id) {
        try {
            SysHelpFaqVO sysHelpFaqVO = faqService.get(id);
            return R.data(sysHelpFaqVO);
        } catch (Exception e) {
            logger.error("查询一个帮助中心条目失败", e);
            return R.error("查询一个帮助中心条目失败");
        }
    }


}
