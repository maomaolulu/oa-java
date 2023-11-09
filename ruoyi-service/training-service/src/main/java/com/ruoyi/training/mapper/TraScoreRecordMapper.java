package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.training.entity.TraScoreRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 成绩记录
 *
 * @author zx
 * @date 2022-6-7 14:49:28
 */
@Repository
public interface TraScoreRecordMapper extends BaseMapper<TraScoreRecord> {
    /**
     * 提交试卷更新个人考试信息
     *
     * @param id
     * @return
     */
    @Update("update tra_course_exam set status = #{status},score = #{score} where exam_id = #{id} ")
    int updateExamInfo(@Param("status") Integer status, @Param("score") Double score, @Param("id") Long id);


    /**
     * 提交试卷更新个人考试信息(自定义考试提交-新)
     *
     * @param id
     * @return
     */
    @Update("update tra_course_exam set status = #{status},score = #{score} where exam_id = #{id} and user_id=#{userId} ")
    int updateExamInfoNew(@Param("status") Integer status, @Param("score") Double score, @Param("id") Long id, @Param("userId") Long userId);
}
