package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.training.entity.TraCourseExam;
import com.ruoyi.training.entity.TraMyExam;
import com.ruoyi.training.entity.TraScoreRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author hjy
 * @date 2022/6/13 16:27
 */
@Repository
public interface TraMyExamMapper extends BaseMapper<TraCourseExam> {
    @Select("select user_id where tra_course_exam  ")
    List<Long> selectUserIds();
    /**
     * 查询我的考试列表
     *
     * @param myExam 我的信息
     * @return 考试列表信息
     */
    List<TraMyExam> selectMyExamList(TraMyExam myExam);

    /**
     * 根据条件查询是否存在该考试类别
     *
     * @param traMyExam 考试类别信息
     * @return 考试信息集合
     */
    TraMyExam selectMyExamByUserIdAndYearAndTrainId(TraMyExam traMyExam);

    /**
     * 新增我的考试类别
     *
     * @param traMyExam 考试类别信息
     * @return 状态
     */
    int insertMyExam(TraMyExam traMyExam);

    /**
     * 更新考试信息
     *
     * @param traMyExam 考试类别信息
     * @return 状态
     */
    int updateMyExamById(TraMyExam traMyExam);

    /**
     * 根据条件删除我的考试类别
     *
     * @param userId  用户id
     * @param nowYear 当前年份
     * @param trainId 考试类别
     * @return 状态
     */
    int deleteMyExam(@Param("userId") Long userId, @Param("nowYear") int nowYear, @Param("trainId") Long trainId);


    /**
     * 更新我的考试状态
     *
     * @param myExam 相关属性信息
     * @return 状态
     */
    int updateMyExam(TraMyExam myExam);

    /**
     * 通过id查询我的考试信息
     *
     * @param id 主键
     * @return 我的考试信息
     */
    TraMyExam selectMyExamById(Long id);

    /**
     * 获取员工培训情况-考试部分
     *
     * @param userId  用户id
     * @param nowYear 考核年份
     * @return 考试信息集合
     */
    List<Map<String, Object>> selectMyExamListInfoByUserId(@Param("userId") Long userId, @Param("nowYear") Integer nowYear);

    /**
     * 查找当期考试是否存在
     *
     * @param userId  用户id
     * @param nowYear 当期那年份
     * @return 考试信息
     */
    TraMyExam findExam(@Param("userId") Long userId, @Param("nowYear") Integer nowYear);

    /**
     * 将课程绑定到考试
     *
     * @param examId   考试id
     * @param courseId 课程id
     */
    void addCourse(@Param("examId") Long examId, @Param("courseId") Long courseId);

    /**
     * 解除考试绑定的课程（自定义考试中的课程无法去除）
     *
     * @param examId   考试id
     * @param courseId 课程id
     */
    void deleteCourse(@Param("examId") Long examId, @Param("courseId") Long courseId);

    /**
     * 统计当前考试绑定的课程
     *
     * @param examId 考试id
     * @return 课程数量
     */
    int selectMyExamCourseCount(Long examId);

    /**
     * 删除我的考试-通过考试id
     *
     * @param examId 考试id
     */
    void deleteMyExamById(Long examId);
}
