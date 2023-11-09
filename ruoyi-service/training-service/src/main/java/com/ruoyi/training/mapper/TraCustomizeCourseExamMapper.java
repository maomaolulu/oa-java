package com.ruoyi.training.mapper;

import java.util.List;

import com.ruoyi.training.entity.TraCustomizeCourseExam;
import com.ruoyi.training.entity.dto.TraCustomizeExamDTO;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 考试涉及的课程Mapper接口
 * 
 * @author yrb
 * @date 2022-10-21
 */
@Repository
public interface TraCustomizeCourseExamMapper extends BaseMapper<TraCustomizeCourseExam>
{
    /**
     * 查询考试涉及的课程
     * 
     * @param examId 考试涉及的课程主键
     * @return 考试涉及的课程
     */
    TraCustomizeCourseExam selectTraCustomizeCourseExamByExamId(Long examId);

    /**
     * 查询考试涉及的课程列表
     * 
     * @param traCustomizeCourseExam 考试涉及的课程
     * @return 考试涉及的课程集合
     */
    List<TraCustomizeCourseExam> selectTraCustomizeCourseExamList(TraCustomizeCourseExam traCustomizeCourseExam);

    /**
     * 新增考试涉及的课程
     * 
     * @param traCustomizeCourseExam 考试涉及的课程
     * @return 结果
     */
    int insertTraCustomizeCourseExam(TraCustomizeCourseExam traCustomizeCourseExam);

    /**
     * 修改考试涉及的课程
     * 
     * @param traCustomizeCourseExam 考试涉及的课程
     * @return 结果
     */
    int updateTraCustomizeCourseExam(TraCustomizeCourseExam traCustomizeCourseExam);

    /**
     * 删除考试涉及的课程
     * 
     * @param examId 考试涉及的课程主键
     * @return 结果
     */
    int deleteTraCustomizeCourseExamByExamId(Long examId);

    /**
     * 批量删除考试涉及的课程
     * 
     * @param examIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTraCustomizeCourseExamByExamIds(Long[] examIds);

    /**
     * 批量插入考试-课程信息
     *
     * @param list 考试-课程信息集合
     * @return result
     */
    int insertBatch(List<TraCustomizeCourseExam> list);

    /**
     * 根据考试ID获取课程ID
     *
     * @param examId 考试ID
     * @return result
     */
    List<Long> selectCourseIds(Long examId);

    /**
     * 查询考试状态
     *
     * @param traCustomizeExamDTO 用户id 考试id
     * @return result
     */
    List<Integer> selectCustomizeExamStatus(TraCustomizeExamDTO traCustomizeExamDTO);
}
