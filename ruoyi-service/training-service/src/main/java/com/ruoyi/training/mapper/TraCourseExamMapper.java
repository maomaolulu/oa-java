package com.ruoyi.training.mapper;

import java.util.List;

import com.ruoyi.training.entity.TraCourseExam;
import com.ruoyi.training.entity.vo.TraCourseExamVO;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 我的考试Mapper接口
 * 
 * @author yrb
 * @date 2022-10-21
 */
@Repository
public interface TraCourseExamMapper extends BaseMapper<TraCourseExam>
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
    List<TraCourseExam> selectTraCourseExamList(TraCourseExam traCourseExam);

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
     * 删除我的考试
     * 
     * @param id 我的考试主键
     * @return 结果
     */
    int deleteTraCourseExamById(Long id);

    /**
     * 批量删除我的考试
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTraCourseExamByIds(Long[] ids);

    /**
     * 查询我的考试列表(自定义页面)
     *
     * @param traCourseExam 我的考试
     * @return 我的考试集合
     */
    List<TraCourseExamVO> selectTraCourseExamUserList(TraCourseExam traCourseExam);
}
