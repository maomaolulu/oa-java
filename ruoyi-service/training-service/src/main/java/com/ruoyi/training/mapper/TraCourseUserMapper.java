package com.ruoyi.training.mapper;

import com.ruoyi.training.entity.TraCourseUser;
import com.ruoyi.training.entity.vo.CourseUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 我的课程 mapper
 *
 * @author hjy
 */
@Repository
public interface TraCourseUserMapper {


    /**
     * 查询我的课程
     *
     * @param traCourseUser 我的课程信息
     * @return 我饿课程列表
     */
    List<TraCourseUser> selectMyCourseInfoList(TraCourseUser traCourseUser);

    /**
     * 新增我的课程
     *
     * @param traCourseUser 我的课程信息
     * @return 添加状态
     */
    int insertMyCourse(TraCourseUser traCourseUser);

    /**
     * 根据条件删除我的课程
     *
     * @param userId   用户id
     * @param nowYear  当前年份
     * @param courseId 课程id
     * @return 状态
     */
    int deleteMyCourse(@Param("userId") Long userId, @Param("nowYear") int nowYear, @Param("courseId") Long courseId);

    /**
     * 更新我的课程进度以及状态
     *
     * @param traCourseUser 我的课程信息
     * @return 状态
     */
    int updateTraCourseUser(TraCourseUser traCourseUser);

    /**
     * 查询证书相关联的课程-作废
     *
     * @param userId  用户id
     * @param trainId 课程类别
     * @return 课程列表
     */
    List<CourseUserVO> selectMyCourseInfoListForCertificate(@Param("userId") Long userId, @Param("trainId") Long trainId);
}
