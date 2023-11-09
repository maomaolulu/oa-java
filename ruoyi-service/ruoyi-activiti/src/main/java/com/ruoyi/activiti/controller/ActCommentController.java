package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.ActComment;
import com.ruoyi.activiti.service.ActCommentService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程评论
 *
 * @author zx
 * @date 2022/2/21 14:56
 * @menu 流程评论
 */
@RestController
@RequestMapping("comment")
public class ActCommentController extends BaseController {
    private final ActCommentService actCommentService;

    @Autowired
    public ActCommentController(ActCommentService actCommentService) {
        this.actCommentService = actCommentService;
    }

    @PostMapping
    @OperLog(title = "新增评论", businessType = BusinessType.INSERT)
    public R save(@RequestBody ActComment actComment) {
        try {
            actComment = actCommentService.save(actComment);
            if (actComment != null) {
                return R.data(actComment);
            }
            return R.error("新增评论失败");
        } catch (Exception e) {
            return R.error("新增评论失败");
        }
    }

    @DeleteMapping("/{id}")
    @OperLog(title = "撤回评论", businessType = BusinessType.UPDATE)
    public R del(@PathVariable("id") Long id) {
        try {
            ActComment actComment = actCommentService.delete(id);
            if (actComment != null) {
                return R.data(actComment);
            }
            return R.error("撤销评论失败");
        } catch (Exception e) {
            return R.error("撤销评论失败");
        }
    }

//    @GetMapping("/{procInstId}")
//    public R del(@PathVariable("procInstId")String procInstId){
//        try {
//            int count = actCommentService.count(procInstId);
//            return R.data(count);
//        }catch (Exception e){
//            return R.error("统计评论失败");
//        }
//    }
//
//    @GetMapping("/{procInstId}")
//    public R list(@PathVariable("procInstId")String procInstId){
//        try {
//            List<ActComment> list = actCommentService.list(procInstId);
//            return R.data(list);
//        }catch (Exception e){
//            return R.error("获取评论失败");
//        }
//    }


}
