package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCourseUser;
import com.ruoyi.training.entity.TraMyHours;
import com.ruoyi.training.service.ITraCourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 我的课程 Controller
 *
 * @author hjy
 * @date 2022/6/13 14:18
 */

@RestController
@RequestMapping("/training/user")
public class TraCourseUserController extends BaseController {

    private final ITraCourseUserService courseUserService;

    @Autowired
    public TraCourseUserController(ITraCourseUserService courseUserService) {
        this.courseUserService = courseUserService;
    }

    /**
     * 查询本部门全部课程信息列表
     * 变更-获取公司全部课程,选中部门为必修部门
     */
    @GetMapping("/list")
    public R list(TraCourseInfo traCourseInfo) {
        try {
            pageUtil();
            List<TraCourseInfo> list = courseUserService.selectTraCourseInfoList(traCourseInfo);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询本部门全部课程信息列表失败，异常信息：" + e);
            return R.error("查询本部门全部课程信息列表失败");
        }
    }


    /**
     * 查询我已选择课程信息列表
     */
    @GetMapping("/myList")
    public R myList(TraCourseUser traCourseUser) {
        try {
            pageUtil();
            List<TraCourseUser> list = courseUserService.selectMyCourseInfoList(traCourseUser);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询我的课程信息列表失败，异常信息：" + e);
            return R.error("查询我的课程信息列表失败");
        }
    }

    /**
     * 查询课程类型列表
     *
     * @param type 1：我的课程类型列表；0：本部门课程类型列表;必填
     */
    @GetMapping("getTypeList")
    public R getTypeList(Integer type) {
        try {
            return R.ok("列表查询成功！", courseUserService.getTypeList(type));
        } catch (Exception e) {
            logger.error("课程类型列表查询失败：" + e);
            return R.error("课程类型列表查询失败！");
        }
    }


    /**
     * 更新我的课程进度以及状态
     */
    @PutMapping
    public R update(@RequestBody TraCourseUser traCourseUser) {
        try {
            if (null == traCourseUser) {
                return R.error("课程信息不能为空！");
            }
            if (null == traCourseUser.getCourseId()) {
                return R.error("课程id不能为空！");
            }
            if (courseUserService.updateTraCourseUser(traCourseUser) > 0) {
                return R.ok("我的课程更新成功！");
            }
            return R.error("我的课程更新失败！");
        } catch (Exception e) {
            logger.error("查询失败：" + e);
            return R.error("查询失败！");
        }
    }


    /**
     * 加入课表
     *
     * @param traCourseUser 课表信息
     * @return 状态
     */
    @PostMapping("addMyExam")
    public R addMyExam(@RequestBody TraCourseUser traCourseUser) {
        try {
            if (null == traCourseUser.getCourseId()) {
                return R.error("课程id不能为空！");
            }
            if (courseUserService.insertMyCourse(traCourseUser) > 0) {
                return R.ok("加入课表成功！");
            }
            return R.error("加入课表失败！");
        } catch (Exception e) {
            logger.error("加入课表失败，异常信息：" + e);
            return R.error("加入课表失败");
        }
    }

    /**
     * 删除我的课程
     *
     * @param ids 课程id
     * @return 结果状态
     */
    @DeleteMapping("/{ids}")
    public R deleteMyExam(@PathVariable Long ids) {
        try {
            if (ids == null) {
                return R.error("课程id不能为空！");
            }
            if (courseUserService.deleteMyCourse(ids) > 0) {
                return R.ok("删除课程成功！");
            }
            return R.error("删除课程失败！");
        } catch (Exception e) {
            logger.error("删除课程失败，异常信息：" + e);
            return R.error("删除课程失败");
        }
    }


    /**
     * 获取我的学时
     *
     * @param myHours 我的学时相关信息
     * @return 我的学时相关信息
     */
    @PostMapping("getMyHours")
    public R getMyHours(TraMyHours myHours) {
        try {
            return R.ok("我的学时查询成功！", courseUserService.getMyHours(myHours));
        } catch (Exception e) {
            logger.error("我的学时查询失败：" + e);
            return R.error("我的学时查询失败！");
        }
    }


}
