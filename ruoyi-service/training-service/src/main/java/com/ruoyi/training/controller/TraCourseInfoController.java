package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.dto.TraCourseInfoDTO;
import com.ruoyi.training.entity.vo.CourseInfoVO;
import com.ruoyi.training.service.ITraCourseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 课程信息Controller
 *
 * @author yrb
 * @date 2022-05-30
 */
@RestController
@RequestMapping("/training/info")
public class TraCourseInfoController extends BaseController {
    private final ITraCourseInfoService traCourseInfoService;

    @Autowired
    public TraCourseInfoController(ITraCourseInfoService traCourseInfoService) {
        this.traCourseInfoService = traCourseInfoService;
    }

    /**
     * 查询课程信息列表
     */
    @GetMapping("/list")
    public R list(TraCourseInfoDTO traCourseInfoDTO) {
        try {
            pageUtil();
            List<CourseInfoVO> list = traCourseInfoService.selectTraCourseInfoUserList(traCourseInfoDTO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询课程信息列表失败，异常信息：" + e);
            return R.error("查询课程信息列表失败");
        }
    }

    /**
     * 查询公司关联课程
     */
    @GetMapping("/companyCourseInfoList")
    public R companyCourseInfoList(Long companyId) {
        List<TraCourseInfo> traCourseInfos = traCourseInfoService.companyCourseInfoList(companyId);
        return R.ok("list",traCourseInfos);

    }
    /**
     * 批量查询公司关联课程
     */
    @GetMapping("/companyCourseInfoLists/{ids}")
    public R companyCourseInfoLists(@PathVariable Long[] ids) {

        if (null == ids || ids.length < 1) {
            return R.error("公司ids不能为空");
        }
        Map<Long, List<TraCourseInfo>> map = traCourseInfoService.companyCourseInfoLists(Arrays.asList(ids));
        return R.ok("map",map);

    }

    /**
     * 获取课程信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("课程id不能为空！");
            }
            return R.data(traCourseInfoService.selectTraCourseInfoById(id));
        } catch (Exception e) {
            logger.error("查询课程信息失败，异常信息：" + e);
            return R.error("查询课程信息失败");
        }

    }

    /**
     * 新增课程信息
     */
    @OperLog(title = "课程信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody TraCourseInfo traCourseInfo) {
        try {
            if (null == traCourseInfo) {
                return R.error("课程信息不能为空！");
            }
            Map<String, Object> map = traCourseInfoService.insertTraCourseInfo(traCourseInfo);
            if ((int) map.get("result") > 0) {
                return R.ok("课程信息新增成功！", map.get("id").toString());
            }
            return R.error("课程信息新增失败！");
        } catch (Exception e) {
            logger.error("新增课程信息失败，异常信息：" + e);
            return R.error("新增课程信息失败");
        }
    }

    /**
     * 修改课程信息
     */
    @OperLog(title = "课程信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraCourseInfo traCourseInfo) {
        try {
            if (null == traCourseInfo) {
                return R.error("课程信息不能为空！");
            }
            if (null == traCourseInfo.getId()) {
                return R.error("课程信息id不能为空！");
            }
            if (traCourseInfoService.updateTraCourseInfo(traCourseInfo) > 0) {
                return R.ok("课程信息更新成功！");
            }
            return R.error("课程信息更新失败！");
        } catch (Exception e) {
            logger.error("新增课程信息失败，异常信息：" + e);
            return R.error("新增课程信息失败");
        }
    }

    /**
     * 删除课程信息
     */
    @OperLog(title = "课程信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的课程信息id不能为空！");
            }
            if (traCourseInfoService.deleteTraCourseInfoByIds(ids) > 0) {
                return R.ok("课程信息删除成功！");
            }
            return R.error("课程信息删除失败！");
        } catch (Exception e) {
            logger.error("课程信息删除失败，异常信息：" + e);
            return R.error("课程信息删除失败");
        }
    }

    /**
     * 查询全部课程
     */
    @GetMapping("/allCourse")
    public R allCourse(TraCourseInfoDTO traCourseInfoDTO) {
        try {
            pageUtil();
            List<CourseInfoVO> list = traCourseInfoService.findTraCourseInfoList(traCourseInfoDTO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询全部课程信息列表失败，异常信息：" + e);
            return R.error("查询全部课程信息列表失败");
        }
    }

    /**
     * 查询我的课表
     */
    @GetMapping("/myCourse")
    public R myCourse(TraCourseInfoDTO traCourseInfoDTO) {
        try {
            pageUtil();
            List<CourseInfoVO> list = traCourseInfoService.findMyCourseList(traCourseInfoDTO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询我的课表信息失败，异常信息：" + e);
            return R.error("查询我的课表信息失败");
        }
    }

    /**
     * 修改课程上下架状态
     */
    @OperLog(title = "课程信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeCourseMarketState")
    public R changeCourseMarketState(@RequestBody TraCourseInfo traCourseInfo) {
        try {
            if (traCourseInfo.getId() == null) {
                return R.error("课程id不能为空");
            }
            if (traCourseInfo.getIssueFlag() == null) {
                return R.error("上下架状态不能为空");
            }
            if (traCourseInfoService.changeCourseMarketState(traCourseInfo)) {
                return R.ok("成功");
            }
            return R.error("失败");
        } catch (Exception e) {
            logger.error("发生异常，异常信息：" + e);
            return R.error("未知错误，请联系管理员");
        }
    }
}
