package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraMyExam;
import com.ruoyi.training.service.ITraMyExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 我的考试Controller
 *
 * @author hjy
 * @date 2022/6/13 16:13
 */

@RestController
@RequestMapping("/training/exam")
public class TraMyExamController extends BaseController {

    private final ITraMyExamService myExamService;

    @Autowired
    public TraMyExamController(ITraMyExamService myExamService) {
        this.myExamService = myExamService;
    }

    /**
     * 我的考试列表
     */
    @GetMapping("/list")
    public R list(TraMyExam myExam) {
        try {
            List<TraMyExam> list = myExamService.selectMyExamList(myExam);
            return R.data(list);
        } catch (Exception e) {
            logger.error("查询我的考试信息列表失败，异常信息：" + e);
            return R.error("查询我的考试信息列表失败");
        }
    }

    /**
     * 获取我的考试信息详情
     */
    @GetMapping("/getExamInfo")
    public R getExamInfo(Long examId) {
        try {
            return R.data(myExamService.selectMyExamById(examId));
        } catch (Exception e) {
            logger.error("查询我的考试信息详情失败，异常信息：" + e);
            return R.error("查询我的考试信息详情失败");
        }
    }

}
