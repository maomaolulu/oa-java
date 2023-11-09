package com.ruoyi.training.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCustomizeExam;
import com.ruoyi.training.entity.dto.TraCustomizeExamDTO;
import com.ruoyi.training.entity.vo.TraCustomizeExamVO;
import com.ruoyi.training.entity.vo.TraCustomizeExamsVO;

/**
 * 自定义考试信息Service接口
 *
 * @author yrb
 * @date 2022-10-19
 */
public interface ITraCustomizeExamService extends IService<TraCustomizeExam> {
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
     * @param traCustomizeExamVO 自定义考试信息
     * @return 自定义考试信息集合
     */
    List<TraCustomizeExamVO> selectTraCustomizeExamList(TraCustomizeExamVO traCustomizeExamVO);

    /**
     * 新增自定义考试信息
     *
     * @param traCustomizeExamDTO 自定义考试信息
     * @return 结果
     */
    boolean insertTraCustomizeExam(TraCustomizeExamDTO traCustomizeExamDTO);

    /**
     * 修改自定义考试信息
     *
     * @param traCustomizeExamDTO 自定义考试信息
     * @return 结果
     */
    boolean updateTraCustomizeExam(TraCustomizeExamDTO traCustomizeExamDTO);

    /**
     * 批量删除自定义考试信息
     *
     * @param ids 需要删除的自定义考试信息主键集合
     * @return 结果
     */
    int deleteTraCustomizeExamByIds(Long[] ids);

    /**
     * 删除自定义考试信息信息
     *
     * @param id 自定义考试信息主键
     * @return 结果
     */
    int deleteTraCustomizeExamById(Long id);

    /**
     * 考试信息已发布 修改人员个数
     *
     * @param traCustomizeExamDTO 人员个数
     * @return result
     */
    boolean editExamUser(TraCustomizeExamDTO traCustomizeExamDTO);

    /**
     * 发布考试信息
     *
     * @param traCustomizeExamDTO 考试信息
     * @return result
     */
    boolean issueExamInfo(TraCustomizeExamDTO traCustomizeExamDTO);

    /**
     * 获取课程信息列表
     *
     * @param examId 考试信息ID
     * @return  result
     */
    List<TraCourseInfo> getCourseInfoList(Long examId,Long companyId);

    /**
     * 获取考试信息
     *
     * @param examId 考试id
     * @return result
     */
    TraCustomizeExamsVO getCustomizeExamInfo(Long examId);
}
