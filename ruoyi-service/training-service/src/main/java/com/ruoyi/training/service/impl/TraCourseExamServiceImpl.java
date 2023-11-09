package com.ruoyi.training.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCourseExam;
import com.ruoyi.training.entity.TraTrainCategory;
import com.ruoyi.training.entity.dto.TraCustomizeExamDTO;
import com.ruoyi.training.entity.vo.TraCourseExamVO;
import com.ruoyi.training.mapper.TraCustomizeCourseExamMapper;
import com.ruoyi.training.mapper.TraTrainCategoryMapper;
import com.ruoyi.training.utils.TrainingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.training.mapper.TraCourseExamMapper;
import com.ruoyi.training.service.ITraCourseExamService;

/**
 * 我的考试Service业务层处理
 *
 * @author yrb
 * @date 2022-10-21
 */
@Service
public class TraCourseExamServiceImpl extends ServiceImpl<TraCourseExamMapper, TraCourseExam> implements ITraCourseExamService {
    private final TraCourseExamMapper traCourseExamMapper;
    private final TraTrainCategoryMapper traTrainCategoryMapper;
    private final TraCustomizeCourseExamMapper traCustomizeCourseExamMapper;

    @Autowired
    public TraCourseExamServiceImpl(TraCourseExamMapper traCourseExamMapper,
                                    TraTrainCategoryMapper traTrainCategoryMapper,
                                    TraCustomizeCourseExamMapper traCustomizeCourseExamMapper) {
        this.traCourseExamMapper = traCourseExamMapper;
        this.traTrainCategoryMapper = traTrainCategoryMapper;
        this.traCustomizeCourseExamMapper = traCustomizeCourseExamMapper;
    }

    /**
     * 查询我的考试
     *
     * @param id 我的考试主键
     * @return 我的考试
     */
    @Override
    public TraCourseExam selectTraCourseExamById(Long id) {
        return traCourseExamMapper.selectTraCourseExamById(id);
    }

    /**
     * 查询我的考试列表
     *
     * @param traCourseExam 我的考试
     * @return 我的考试
     */
    @Override
    public List<TraCourseExamVO> selectTraCourseExamList(TraCourseExam traCourseExam) {
        TraCourseExam courseExam = new TraCourseExam();
        Long userId = SystemUtil.getUserId();
        courseExam.setUserId(userId);
        courseExam.setExamType(1L);
        List<TraCourseExamVO> traCourseExamVOList = traCourseExamMapper.selectTraCourseExamUserList(courseExam);
        if (CollUtil.isNotEmpty(traCourseExamVOList)) {
            for (TraCourseExamVO traCourseExamVO : traCourseExamVOList) {
                Long examId = traCourseExamVO.getExamId();
                if (examId == null) throw new RuntimeException("考试id不能为空");
                // 获取课程所属分类及颜色
                List<TraTrainCategory> traTrainCategoryList = traTrainCategoryMapper.selectTraTrainCategoryUserList(examId);
                if (CollUtil.isEmpty(traTrainCategoryList)) throw new RuntimeException("未获取到课程分类信息");
                List<String> list = new ArrayList<>();
                for (TraTrainCategory traTrainCategory : traTrainCategoryList) {
                    list.add(traTrainCategory.getCategoryName() + "@" + traTrainCategory.getCssClass());
                }
                traCourseExamVO.setCategoryList(TrainingUtils.getFormatString(list, ","));
                // 判断是否可以考试
                TraCustomizeExamDTO examDTO = new TraCustomizeExamDTO();
                examDTO.setUserId(userId);
                examDTO.setExamId(examId);
                List<Integer> statusList = traCustomizeCourseExamMapper.selectCustomizeExamStatus(examDTO);
                if (statusList.size() == 1 && statusList.get(0) == 2) {
                    // 1 可以考试
                    traCourseExamVO.setChecked(1);
                } else {
                    // 0 不可考试
                    traCourseExamVO.setChecked(0);
                }
            }
        }
        return traCourseExamVOList;
    }

    /**
     * 新增我的考试
     *
     * @param traCourseExam 我的考试
     * @return 结果
     */
    @Override
    public int insertTraCourseExam(TraCourseExam traCourseExam) {
        return traCourseExamMapper.insertTraCourseExam(traCourseExam);
    }

    /**
     * 修改我的考试
     *
     * @param traCourseExam 我的考试
     * @return 结果
     */
    @Override
    public int updateTraCourseExam(TraCourseExam traCourseExam) {
        return traCourseExamMapper.updateTraCourseExam(traCourseExam);
    }

    /**
     * 批量删除我的考试
     *
     * @param ids 需要删除的我的考试主键
     * @return 结果
     */
    @Override
    public int deleteTraCourseExamByIds(Long[] ids) {
        return traCourseExamMapper.deleteTraCourseExamByIds(ids);
    }

    /**
     * 删除我的考试信息
     *
     * @param id 我的考试主键
     * @return 结果
     */
    @Override
    public int deleteTraCourseExamById(Long id) {
        return traCourseExamMapper.deleteTraCourseExamById(id);
    }
}
