package com.ruoyi.training.service.impl;

import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraMyExam;
import com.ruoyi.training.mapper.TraMyExamMapper;
import com.ruoyi.training.service.ITraMyExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hjy
 */
@Service
public class TraMyExamServiceImpl implements ITraMyExamService {

    private final TraMyExamMapper myExamMapper;

    @Autowired
    public TraMyExamServiceImpl(TraMyExamMapper myExamMapper) {
        this.myExamMapper = myExamMapper;
    }

    /**
     *
     * 我的考试列表
     * @param myExam 我的信息
     * @return 考试列表集合
     */
    @Override
    public List<TraMyExam> selectMyExamList(TraMyExam myExam) {
        //加入人员筛选
        if (myExam.getUserId()==null){
            myExam.setUserId(SystemUtil.getUserId());
        }
        // 查询年度考核（0表示年度考核）
        myExam.setExamType(0);
        return myExamMapper.selectMyExamList(myExam);
    }

    /**
     * 更新我的考试状态
     * @param myExam 相关属性信息
     * @return 状态
     */
    @Override
    public int updateMyExam(TraMyExam myExam) {
        return myExamMapper.updateMyExam(myExam);
    }

    /**
     * 通过id查询我的考试信息
     * @param id 主键
     * @return 我的考试信息
     */
    @Override
    public TraMyExam selectMyExamById(Long id) {
        return myExamMapper.selectMyExamById(id);
    }
}
