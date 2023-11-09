package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.training.entity.TraQuestionInfo;
import com.ruoyi.training.entity.bo.QuestionBO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


/**
 * 题目表
 *
 * @author : zx
 * @date : 2022-5-30
 */
@Repository
public interface TraQuestionInfoMapper extends BaseMapper<TraQuestionInfo> {
    /**
     * 新增课程题目管理信息
     *
     * @param courseId   课程id
     * @param questionId 题目id
     * @return int
     */
    @Insert(" INSERT INTO tra_course_question (course_id,question_id) VALUES (#{courseId},#{questionId})")
    int insertCourseTraQuestion(@Param("courseId") BigInteger courseId, @Param("questionId") BigInteger questionId);

    /**
     * 根据题目id删除课程题目管理信息
     * @param id 题目id
     * @return int
     */
    @Delete("DELETE FROM tra_course_question WHERE question_id = #{id}")
    int deleteCourse(@Param("id") BigInteger id);

    /**
     * 查询试题
     * @param queryWrapper
     * @return
     */
    @Select("SELECT d.dept_id,d.dept_name,a.id,a.content,group_concat(b.course_name separator ';') as course_name,c.category_name,a.update_by,a.update_time,a.answer,a.score,a.type " +
            "FROM  " +
            "tra_question_info a " +
            "LEFT JOIN tra_course_question cq on cq.question_id  = a.id " +
            "LEFT JOIN tra_course_info b on b.id = cq.course_id " +
            "LEFT JOIN tra_train_category c on c.id = b.train_id " +
            "LEFT JOIN sys_dept d on c.company_id = d.dept_id ${ew.customSqlSegment} ")
    List<QuestionBO> questionList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    @Select("select * from tra_question_info limit 1 ")
    Optional<TraQuestionInfo> selectQuestionById();
}