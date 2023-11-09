package com.ruoyi.training.controller;

import java.util.List;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraCourseExam;
import com.ruoyi.training.entity.vo.TraCourseExamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.training.service.ITraCourseExamService;

/**
 * 我的考试Controller
 * 
 * @author yrb
 * @date 2022-10-21
 */
@RestController
@RequestMapping("/training/customize/myexam")
public class TraCourseExamController extends BaseController
{

    private final ITraCourseExamService traCourseExamService;

    @Autowired
    public TraCourseExamController(ITraCourseExamService traCourseExamService) {
        this.traCourseExamService = traCourseExamService;
    }

    /**
     * 查询我的考试列表
     */
    @GetMapping("/list")
    public R list(TraCourseExam traCourseExam)
    {
        try {
            pageUtil();
            List<TraCourseExamVO> list = traCourseExamService.selectTraCourseExamList(traCourseExam);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息："+e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取我的考试详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id)
    {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(traCourseExamService.selectTraCourseExamById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增我的考试
     */
    @OperLog(title = "我的考试", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody TraCourseExam traCourseExam)
    {
        try {
            if (traCourseExamService.insertTraCourseExam(traCourseExam) > 0) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 修改我的考试
     */
    @OperLog(title = "我的考试", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraCourseExam traCourseExam)
    {
        try {
            if (null == traCourseExam.getId()) {
                return R.error("id不能为空！");
            }
            if (traCourseExamService.updateTraCourseExam(traCourseExam) > 0) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除我的考试
     */
    @OperLog(title = "我的考试", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids)
    {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (traCourseExamService.deleteTraCourseExamByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息："+e);
            return R.error("删除失败!");
        }
    }
}
