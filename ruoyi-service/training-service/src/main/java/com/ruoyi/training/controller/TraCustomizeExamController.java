package com.ruoyi.training.controller;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCustomizeExam;
import com.ruoyi.training.entity.dto.TraCustomizeExamDTO;
import com.ruoyi.training.entity.vo.TraCustomizeExamVO;
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
import com.ruoyi.training.service.ITraCustomizeExamService;

/**
 * 自定义考试信息Controller
 *
 * @author yrb
 * @date 2022-10-19
 */
@RestController
@RequestMapping("/training/customize/exam")
public class TraCustomizeExamController extends BaseController {
    private final ITraCustomizeExamService traCustomizeExamService;

    @Autowired
    public TraCustomizeExamController(ITraCustomizeExamService traCustomizeExamService) {
        this.traCustomizeExamService = traCustomizeExamService;
    }

    /**
     * 查询自定义考试信息列表
     */
    @GetMapping("/list")
    public R list(TraCustomizeExamVO traCustomizeExamVO) {
        try {
            pageUtil();
            List<TraCustomizeExamVO> list = traCustomizeExamService.selectTraCustomizeExamList(traCustomizeExamVO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取自定义考试信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(traCustomizeExamService.selectTraCustomizeExamById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增自定义考试信息
     */
    @OperLog(title = "自定义考试信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody TraCustomizeExamDTO traCustomizeExamDTO) {
        try {
            if (StrUtil.isBlank(traCustomizeExamDTO.getExamName())) {
                return R.error("考试名称不能为空");
            }
            if (CollUtil.isEmpty(traCustomizeExamDTO.getCourseIdList())) {
                return R.error("所选课程信息为空，请先选择课程信息");
            }
            if (CollUtil.isEmpty(traCustomizeExamDTO.getUserIdList())) {
                return R.error("所选用户为空，请先选择考试用户");
            }
            if (traCustomizeExamService.insertTraCustomizeExam(traCustomizeExamDTO)) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("发生错误，" + e.getMessage());
        }
    }

    /**
     * 修改自定义考试信息 (未发布-修改所有信息)
     */
    @OperLog(title = "自定义考试信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraCustomizeExamDTO traCustomizeExamDTO) {
        try {
            if (null == traCustomizeExamDTO.getId()) {
                return R.error("id不能为空！");
            }
            if (traCustomizeExamDTO.getIssueFlag() == null) {
                return R.error("发布状态不能为空");
            }
            if (CollUtil.isEmpty(traCustomizeExamDTO.getUserIdList())) {
                return R.error("所选用户不能为空");
            }
            if (CollUtil.isEmpty(traCustomizeExamDTO.getCourseIdList())) {
                return R.error("所选课程信息不能为空");
            }
            if (traCustomizeExamService.updateTraCustomizeExam(traCustomizeExamDTO)) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除自定义考试信息
     */
    @OperLog(title = "自定义考试信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (traCustomizeExamService.deleteTraCustomizeExamByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 已发布 修改参与考试人员
     */
    @OperLog(title = "修改参与考试人员", businessType = BusinessType.UPDATE)
    @PostMapping("/editExamUser")
    public R editExamUser(@RequestBody TraCustomizeExamDTO traCustomizeExamDTO) {
        try {
            if (traCustomizeExamDTO.getId() == null) {
                return R.error("考试信息id不能为空");
            }
            if (CollUtil.isEmpty(traCustomizeExamDTO.getUserIdList())) {
                return R.error("考试人员信息不能为空");
            }
            if (traCustomizeExamService.editExamUser(traCustomizeExamDTO)) {
                return R.ok("编辑考试人员信息成功");
            }
            return R.error("编辑考试人员信息失败");
        } catch (Exception e) {
            logger.error("出现异常，异常信息----->" + e);
            return R.error("未知错误，请联系管理员");
        }
    }

    /**
     * 发布考试信息
     */
    @OperLog(title = "发布考试信息", businessType = BusinessType.UPDATE)
    @PostMapping("/issueExamInfo")
    public R issueExamInfo(@RequestBody TraCustomizeExamDTO traCustomizeExamDTO) {
        try {
            if (traCustomizeExamDTO.getId() == null) {
                return R.error("考试信息id不能为空");
            }
            if (StrUtil.isBlank(traCustomizeExamDTO.getExamName())){
                return R.error("考试名称不能为空");
            }
            if (StrUtil.isBlank(traCustomizeExamDTO.getUserIds())){
                return R.error("用户id不能为空");
            }
            if (traCustomizeExamService.issueExamInfo(traCustomizeExamDTO)) {
                return R.ok("考试信息发布成功");
            }
            return R.error("考试信息发布失败");
        } catch (Exception e) {
            logger.error("考试信息发布失败，异常信息----->" + e);
            return R.error("未知错误，请联系管理员！");
        }
    }


    /**
     * 批量发布考试信息
     */
    @OperLog(title = "批量发布考试信息", businessType = BusinessType.UPDATE)
    @PostMapping("/issueExamInfoBatch")
    public R issueExamInfoBatch(@RequestBody List<TraCustomizeExamDTO> traCustomizeExamDTOs) {
        try {
            for (TraCustomizeExamDTO traCustomizeExamDTO:traCustomizeExamDTOs
            ) {
                traCustomizeExamService.issueExamInfo(traCustomizeExamDTO);
            }
            return R.ok("考试信息发布成功");
        } catch (Exception e) {
            logger.error("考试信息发布失败，异常信息----->" + e);
            return R.error("未知错误，请联系管理员！");
        }
    }

    /**
     * 获取课程信息列表(创建、编辑时使用)
     */
    @GetMapping("/getCourseInfoList")
    public R getCourseInfoList(Long examId,Long companyId) {
        try {
            return R.data(traCustomizeExamService.getCourseInfoList(examId,companyId));
        } catch (Exception e) {
            logger.error("获取课程列表失败，发生异常------->" + e);
            return R.error("未知错误，请联系管理员！");
        }
    }
}
