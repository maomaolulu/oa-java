package com.ruoyi.training.mapper;

import java.util.List;

import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCustomizeExam;
import com.ruoyi.training.entity.vo.TraCustomizeExamVO;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 自定义考试信息Mapper接口
 * 
 * @author yrb
 * @date 2022-10-19
 */
@Repository
public interface TraCustomizeExamMapper extends BaseMapper<TraCustomizeExam>
{
    /**
     * 查询自定义考试信息
     * 
     * @param id 自定义考试信息主键
     * @return 自定义考试信息
     */
    TraCustomizeExam selectTraCustomizeExamById(Long id);

    /**
     * 查询自定义考试信息列表
     * 
     * @param traCustomizeExam 自定义考试信息
     * @return 自定义考试信息集合
     */
    List<TraCustomizeExam> selectTraCustomizeExamList(TraCustomizeExam traCustomizeExam);

    /**
     * 新增自定义考试信息
     * 
     * @param traCustomizeExam 自定义考试信息
     * @return 结果
     */
    int insertTraCustomizeExam(TraCustomizeExam traCustomizeExam);

    /**
     * 修改自定义考试信息
     * 
     * @param traCustomizeExam 自定义考试信息
     * @return 结果
     */
    int updateTraCustomizeExam(TraCustomizeExam traCustomizeExam);

    /**
     * 删除自定义考试信息
     * 
     * @param id 自定义考试信息主键
     * @return 结果
     */
    int deleteTraCustomizeExamById(Long id);

    /**
     * 批量删除自定义考试信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTraCustomizeExamByIds(Long[] ids);

    /**
     * 查询自定义考试信息列表(按更新时间排序)
     *
     * @param traCustomizeExamVO 自定义考试信息
     * @return 自定义考试信息集合
     */
    List<TraCustomizeExamVO> selectTraCustomizeExamUserList(TraCustomizeExamVO traCustomizeExamVO);

    /**
     * 获取关联课程名字
     *
     * @param examId 考试ID
     * @return result
     */
    List<String> selectCourseNames(Long examId);

    /**
     * 获取课程信息（生成试卷时使用）
     *
     * @param examId 考试id
     * @return result
     */
    List<TraCourseInfo> selectCourseInfoForExam(Long examId);
}
