package com.ruoyi.training.mapper;

import java.util.List;

import com.ruoyi.training.entity.TraCustomizeCourseExam;
import com.ruoyi.training.entity.TraCustomizeExamUser;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 考试涉及的课程Mapper接口
 * 
 * @author yrb
 * @date 2022-10-21
 */
@Repository
public interface TraCustomizeExamUserMapper extends BaseMapper<TraCustomizeExamUser>
{
    /**
     * 查询考试涉及的课程
     * 
     * @param examId 考试涉及的课程主键
     * @return 考试涉及的课程
     */
    TraCustomizeExamUser selectTraCustomizeExamUserByExamId(Long examId);

    /**
     * 查询考试涉及的课程列表
     * 
     * @param traCustomizeExamUser 考试涉及的课程
     * @return 考试涉及的课程集合
     */
    List<TraCustomizeExamUser> selectTraCustomizeExamUserList(TraCustomizeExamUser traCustomizeExamUser);

    /**
     * 新增考试涉及的课程
     * 
     * @param traCustomizeExamUser 考试涉及的课程
     * @return 结果
     */
    int insertTraCustomizeExamUser(TraCustomizeExamUser traCustomizeExamUser);

    /**
     * 修改考试涉及的课程
     * 
     * @param traCustomizeExamUser 考试涉及的课程
     * @return 结果
     */
    int updateTraCustomizeExamUser(TraCustomizeExamUser traCustomizeExamUser);

    /**
     * 删除考试涉及的课程
     * 
     * @param examId 考试涉及的课程主键
     * @return 结果
     */
    int deleteTraCustomizeExamUserByExamId(Long examId);

    /**
     * 批量删除考试涉及的课程
     * 
     * @param examIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTraCustomizeExamUserByExamIds(Long[] examIds);

    /**
     * 批量插入考试-用户信息
     *
     * @param list 考试-用户信息集合
     * @return result
     */
    int insertBatch(List<TraCustomizeExamUser> list);

    /**
     * 根据考试ID获取用户ID
     *
     * @param examId 考试ID
     * @return result
     */
    List<Long> selectUserIds(Long examId);
}
