package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraMyExam;

import java.util.List;

/**
 * 我的考试-service
 *
 * @author hjy
 */
public interface ITraMyExamService {
    /**
     * 查询我的考试列表信息
     *
     * @param myExam 我的考试相关属性
     * @return 我的考试列表信息
     */
    List<TraMyExam> selectMyExamList(TraMyExam myExam);

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
}
