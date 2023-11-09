package com.ruoyi.training.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.training.entity.TraCourseExam;
import com.ruoyi.training.entity.vo.TraCourseExamVO;

/**
 * 我的考试Service接口
 * 
 * @author yrb
 * @date 2022-10-21
 */
public interface ITraCourseExamService extends IService<TraCourseExam>
{
    /**
     * 查询我的考试
     * 
     * @param id 我的考试主键
     * @return 我的考试
     */
    TraCourseExam selectTraCourseExamById(Long id);

    /**
     * 查询我的考试列表
     * 
     * @param traCourseExam 我的考试
     * @return 我的考试集合
     */
    List<TraCourseExamVO> selectTraCourseExamList(TraCourseExam traCourseExam);

    /**
     * 新增我的考试
     * 
     * @param traCourseExam 我的考试
     * @return 结果
     */
    int insertTraCourseExam(TraCourseExam traCourseExam);

    /**
     * 修改我的考试
     * 
     * @param traCourseExam 我的考试
     * @return 结果
     */
    int updateTraCourseExam(TraCourseExam traCourseExam);

    /**
     * 批量删除我的考试
     * 
     * @param ids 需要删除的我的考试主键集合
     * @return 结果
     */
    int deleteTraCourseExamByIds(Long[] ids);

    /**
     * 删除我的考试信息
     * 
     * @param id 我的考试主键
     * @return 结果
     */
    int deleteTraCourseExamById(Long id);
}
