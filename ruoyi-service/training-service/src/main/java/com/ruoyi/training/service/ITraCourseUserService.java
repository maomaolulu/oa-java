package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCourseUser;
import com.ruoyi.training.entity.TraMyHours;
import com.ruoyi.training.entity.TraTrainCategory;

import java.util.List;

/**
 * @author hjy
 */
public interface ITraCourseUserService {

    /**
     * 查询本部门课程信息列表，包含课程类别列表
     *
     * @param traCourseInfo 相关课程信息
     * @return 课程列表，课程类别列表
     */
    List<TraCourseInfo> selectTraCourseInfoList(TraCourseInfo traCourseInfo);

    /**
     * 查询我的课程列表
     *
     * @param traCourseUser 我的课程信息
     * @return 课程列表信息
     */
    List<TraCourseUser> selectMyCourseInfoList(TraCourseUser traCourseUser);

    /**
     * 加入我的课表
     *
     * @param traCourseUser 课表信息
     * @return 状态
     */
    int insertMyCourse(TraCourseUser traCourseUser);

    /**
     * 删除我的课程
     *
     * @param courseId 课程id
     * @return 状态
     */
    int deleteMyCourse(Long courseId);

    /**
     * @param type 1：我的课程类型列表；0：本部门课程类型列表
     * @return 我的课程类型列表或本部门全部课程类型列表
     * @description 查询课程类型列表
     */
    List<TraTrainCategory> getTypeList(Integer type);

    /**
     * 更新我的课程进度以及状态
     *
     * @param traCourseUser 我的课程信息
     * @return 状态
     */
    int updateTraCourseUser(TraCourseUser traCourseUser);

    /**
     * 获取我的学时
     *
     * @param myHours 我的学时相关信息
     * @return 我的学时相关信息
     */
    TraMyHours getMyHours(TraMyHours myHours);
}
