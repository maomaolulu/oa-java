package com.ruoyi.training.service.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.*;
import com.ruoyi.training.entity.dto.TraCustomizeExamDTO;
import com.ruoyi.training.entity.vo.TraCustomizeExamVO;
import com.ruoyi.training.entity.vo.TraCustomizeExamsVO;
import com.ruoyi.training.mapper.*;
import com.ruoyi.training.utils.TrainingUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.training.service.ITraCustomizeExamService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 自定义考试信息Service业务层处理
 *
 * @author yrb
 * @date 2022-10-19
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TraCustomizeExamServiceImpl extends ServiceImpl<TraCustomizeExamMapper, TraCustomizeExam> implements ITraCustomizeExamService {
    private final TraCustomizeExamMapper traCustomizeExamMapper;
    private final TraMyExamMapper traMyExamMapper;
    private final TraCustomizeCourseExamMapper traCustomizeCourseExamMapper;
    private final TraCustomizeExamUserMapper traCustomizeExamUserMapper;
    private final TraCourseInfoMapper traCourseInfoMapper;
    private final TraScoreRecordMapper scoreRecordMapper;
    private final TraErrQuestionRecordMapper errQuestionRecordMapper;

    @Autowired
    public TraCustomizeExamServiceImpl(TraCustomizeExamMapper traCustomizeExamMapper,
                                       TraMyExamMapper traMyExamMapper,
                                       TraCustomizeCourseExamMapper traCustomizeCourseExamMapper,
                                       TraCustomizeExamUserMapper traCustomizeExamUserMapper,
                                       TraCourseInfoMapper traCourseInfoMapper, TraScoreRecordMapper scoreRecordMapper, TraErrQuestionRecordMapper errQuestionRecordMapper) {
        this.traCustomizeExamMapper = traCustomizeExamMapper;
        this.traMyExamMapper = traMyExamMapper;
        this.traCustomizeCourseExamMapper = traCustomizeCourseExamMapper;
        this.traCustomizeExamUserMapper = traCustomizeExamUserMapper;
        this.traCourseInfoMapper = traCourseInfoMapper;
        this.scoreRecordMapper = scoreRecordMapper;
        this.errQuestionRecordMapper = errQuestionRecordMapper;
    }

    /**
     * 查询自定义考试信息
     *
     * @param id 自定义考试信息主键
     * @return 自定义考试信息
     */
    @Override
    public TraCustomizeExam selectTraCustomizeExamById(Long id) {
        return traCustomizeExamMapper.selectTraCustomizeExamById(id);
    }

    /**
     * 查询自定义考试信息列表
     *
     * @param traCustomizeExamVO 自定义考试信息
     * @return 自定义考试信息
     */
    @Override
    public List<TraCustomizeExamVO> selectTraCustomizeExamList(TraCustomizeExamVO traCustomizeExamVO) {
//        // 按公司查找考试信息
//        traCustomizeExamVO.setCompanyId(SystemUtil.getCompanyId());
        List<TraCustomizeExamVO> traCustomizeExamVOList = traCustomizeExamMapper.selectTraCustomizeExamUserList(traCustomizeExamVO);
        // 填充课程名称
        if (CollUtil.isNotEmpty(traCustomizeExamVOList)) {
            for (TraCustomizeExamVO exam : traCustomizeExamVOList) {
                Long examId = exam.getId();
                // 获取并设置课程名称
                List<String> names = traCustomizeExamMapper.selectCourseNames(examId);
                exam.setCourseName(TrainingUtils.getFormatString(names, "、"));
                // 获取并设置用户id
                List<Long> userIds = traCustomizeExamUserMapper.selectUserIds(examId);
                exam.setUserIds(TrainingUtils.getFormatString(userIds, ","));
            }
        }
        return traCustomizeExamVOList;
    }

    /**
     * 新增自定义考试信息
     *
     * @param traCustomizeExamDTO 自定义考试信息
     * @return 结果
     */
    @Override
    public boolean insertTraCustomizeExam(TraCustomizeExamDTO traCustomizeExamDTO) {
        // 插入考试信息
        traCustomizeExamDTO.setCompanyId(traCustomizeExamDTO.getCompanyId()!=null?traCustomizeExamDTO.getCompanyId():SystemUtil.getCompanyId());
        traCustomizeExamDTO.setCreateTime(new Date());
        traCustomizeExamDTO.setUpdateTime(new Date());
        traCustomizeExamDTO.setLastModifier(SystemUtil.getUserName());
        if (traCustomizeExamMapper.insertTraCustomizeExam(traCustomizeExamDTO) == 0) return false;
        Long examId = traCustomizeExamDTO.getId();
        // 插入考试-课程信息
        List<Long> traCourseInfoList = traCustomizeExamDTO.getCourseIdList();
        List<TraCustomizeCourseExam> traCustomizeCourseExamList = new ArrayList<>();
        for (Long courseId : traCourseInfoList) {
            TraCustomizeCourseExam courseExam = new TraCustomizeCourseExam();
            courseExam.setExamId(examId);
            courseExam.setCourseId(courseId);
            traCustomizeCourseExamList.add(courseExam);
        }
        if (traCustomizeCourseExamMapper.insertBatch(traCustomizeCourseExamList) == 0)
            throw new RuntimeException("考试关联课程失败");
        // 插入考试-用户信息
        List<Long> userIdList = traCustomizeExamDTO.getUserIdList();
        List<TraCustomizeExamUser> traCustomizeExamUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            TraCustomizeExamUser examUser = new TraCustomizeExamUser();
            examUser.setExamId(examId);
            examUser.setUserId(userId);
            traCustomizeExamUserList.add(examUser);
        }
        if (traCustomizeExamUserMapper.insertBatch(traCustomizeExamUserList) == 0)
            throw new RuntimeException("考试关联用户失败");
        return true;
    }

    /**
     * 修改自定义考试信息
     *
     * @param traCustomizeExamDTO 自定义考试信息
     * @return 结果
     */
    @Override
    public boolean updateTraCustomizeExam(TraCustomizeExamDTO traCustomizeExamDTO) {
        // 获取发布状态
        if (traCustomizeExamDTO.getIssueFlag() != 0) throw new RuntimeException("课程已发布，只能编辑人员");
        // 未发布 都可以改 (修改基本信息)
        traCustomizeExamDTO.setUpdateTime(new Date());
        traCustomizeExamDTO.setLastModifier(SystemUtil.getUserName());
        if (traCustomizeExamMapper.updateTraCustomizeExam(traCustomizeExamDTO) == 0) return false;
        // 删除原有（考试-课程、考试-用户）关联关系
        Long examId = traCustomizeExamDTO.getId();
        if (traCustomizeCourseExamMapper.deleteTraCustomizeCourseExamByExamId(examId) == 0)
            throw new RuntimeException("删除课程关联信息失败");
        if (traCustomizeExamUserMapper.deleteTraCustomizeExamUserByExamId(examId) == 0)
            throw new RuntimeException("删除用户关联信息失败");
        insertExamRelationInfo(examId, traCustomizeExamDTO.getCourseIdList(), traCustomizeExamDTO.getUserIdList());
        return true;
    }

    /**
     * 批量删除自定义考试信息
     *
     * @param ids 需要删除的自定义考试信息主键
     * @return 结果
     */
    @Override
    public int deleteTraCustomizeExamByIds(Long[] ids) {
        return traCustomizeExamMapper.deleteTraCustomizeExamByIds(ids);
    }

    /**
     * 删除自定义考试信息信息
     *
     * @param id 自定义考试信息主键
     * @return 结果
     */
    @Override
    public int deleteTraCustomizeExamById(Long id) {
        return traCustomizeExamMapper.deleteTraCustomizeExamById(id);
    }

    /**
     * 考试信息已发布 修改人员个数
     *
     * @param traCustomizeExamDTO 人员个数
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editExamUser(TraCustomizeExamDTO traCustomizeExamDTO) {
        try {
            Long examId = traCustomizeExamDTO.getId();
            // 删除原有的考试参与人员关联关系
            traCustomizeExamUserMapper.deleteTraCustomizeExamUserByExamId(examId);
            // 插入最新的
            insertExamRelationInfo(examId, null, traCustomizeExamDTO.getUserIdList());
            // 修改人员
            List<Long> newUserIdList = traCustomizeExamDTO.getUserIdList();
            //查出之前人员(正常不为空)  对比出 删除list  新增list
            List<TraCourseExam> oldTraCourseExamList = traMyExamMapper.selectList(new QueryWrapper<TraCourseExam>().eq("exam_id", examId));
            List<Long> userOldList = oldTraCourseExamList.stream().distinct().map(TraCourseExam::getUserId).collect(Collectors.toList());
            //交集
            List<Long> intersection = newUserIdList.stream().filter(userOldList::contains).collect(Collectors.toList());

            //新增
            newUserIdList.removeAll(intersection);
            //删除
            userOldList.removeAll(intersection);

            if(newUserIdList.size()>0){
                for (Long userId : newUserIdList) {
                    TraMyExam traMyExam = new TraMyExam();
                    traMyExam.setUserId(userId);
                    traMyExam.setTrainYear(LocalDate.now().getYear());
                    // 0表示未开始
                    traMyExam.setStatus(0);
                    traMyExam.setExamType(TrainingUtils.TRAINING_CUSTOMIZE_EXAM_TYPE_CUSTOMIZE);
                    traMyExam.setExamName(traCustomizeExamDTO.getExamName());
                    traMyExam.setExamId(examId);
                    traMyExamMapper.insertMyExam(traMyExam);
                }
            }
            if(userOldList.size()>0){
                //删除该人员发布的考试信息
                traMyExamMapper.delete(new QueryWrapper<TraCourseExam>().eq("exam_id",examId).in("user_id",userOldList));
                //关联删除 错题 和 分数记录
                errQuestionRecordMapper.delete(new QueryWrapper<TraErrQuestionRecord>().eq("exam_id",examId).in("user_id",userOldList));
                scoreRecordMapper.delete(new QueryWrapper<TraScoreRecord>().eq("exam_id",examId).in("user_id",userOldList));
            }

            return true;
        }catch (Exception e){
            log.error("考试信息修改失败",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }


    }

    /**
     * 发布考试信息
     *
     * @param traCustomizeExamDTO 考试信息
     * @return result
     */
    @Override
    public boolean issueExamInfo(TraCustomizeExamDTO traCustomizeExamDTO) {
        // 创建用户考试信息
        Long id = traCustomizeExamDTO.getId();
        TraCustomizeExam traCustomizeExam = new TraCustomizeExam();
        traCustomizeExam.setId(id);
        // 1表示已发布
        traCustomizeExam.setIssueFlag(1);
        traCustomizeExam.setLastModifier(SystemUtil.getUserName());
        traCustomizeExam.setUpdateTime(new Date());
        //修改状态为已发布
        if (traCustomizeExamMapper.updateTraCustomizeExam(traCustomizeExam) == 0) return false;

        //将考试人员发布到 统一考试（自定义考试不用于考试查询 需要发布到之前统一考试进行查询获取   ）

        List<String> userIdList = Arrays.asList(traCustomizeExamDTO.getUserIds().split(","));
        if (CollUtil.isEmpty(userIdList)) throw new RuntimeException("获取考试关联用户信息失败");
        for (String userId : userIdList) {
            TraMyExam traMyExam = new TraMyExam();
            traMyExam.setUserId(new Long(userId));
            traMyExam.setTrainYear(LocalDate.now().getYear());
            // 0表示未开始
            traMyExam.setStatus(0);
            traMyExam.setExamType(TrainingUtils.TRAINING_CUSTOMIZE_EXAM_TYPE_CUSTOMIZE);
            traMyExam.setExamName(traCustomizeExamDTO.getExamName());
            traMyExam.setExamId(id);
            if (traMyExamMapper.insertMyExam(traMyExam) == 0) throw new RuntimeException("考试信息发布失败");
        }
        return true;
    }

    /**
     * 获取课程信息列表
     *
     * @param examId 考试信息ID
     * @return result
     */
    @Override
    public List<TraCourseInfo> getCourseInfoList(Long examId,Long companyId) {
        TraCourseInfo courseInfo = new TraCourseInfo();
        courseInfo.setCompanyId(companyId!=null?companyId:SystemUtil.getCompanyId());
        List<TraCourseInfo> traCourseInfoList = traCourseInfoMapper.selectTraCourseInfoList(courseInfo);
        if (examId != null && CollUtil.isNotEmpty(traCourseInfoList)) {
            List<Long> courseIds = traCustomizeCourseExamMapper.selectCourseIds(examId);
            for (TraCourseInfo traCourseInfo : traCourseInfoList) {
                if (courseIds.contains(traCourseInfo.getId())) {
                    // 0表示已选择
                    traCourseInfo.setChecked(0);
                }
            }
        }
        return traCourseInfoList;
    }

    /**
     * 获取考试信息
     *
     * @param examId 考试id
     * @return result
     */
    @Override
    public TraCustomizeExamsVO getCustomizeExamInfo(Long examId) {
        // 通过考试id获取考试信息
        TraCustomizeExam traCustomizeExam = traCustomizeExamMapper.selectTraCustomizeExamById(examId);
        if (traCustomizeExam == null) throw new RuntimeException("获取考试信息失败");
        // 通过考试id获取课程信息
        List<TraCourseInfo> traCourseInfoList = traCustomizeExamMapper.selectCourseInfoForExam(examId);
        if (CollUtil.isEmpty(traCourseInfoList)) throw new RuntimeException("课程信息获取失败");
        // 封装考试信息、课程信息并返回
        TraCustomizeExamsVO traCustomizeExamsVO = new TraCustomizeExamsVO();
        traCustomizeExamsVO.setTraCustomizeExam(traCustomizeExam);
        traCustomizeExamsVO.setTraCourseInfoList(traCourseInfoList);
        return traCustomizeExamsVO;
    }

    /**
     * 插入考试关联信息
     *
     * @param examId          考试信息id
     * @param traCourseIdList 课程ID集合
     * @param userIdList      用户ID集合
     */
    private void insertExamRelationInfo(Long examId, List<Long> traCourseIdList, List<Long> userIdList) {
        //修改 未发布 修改课程 和 自定义人员
        // 插入考试-课程信息
        if (CollUtil.isNotEmpty(traCourseIdList)) {
            List<TraCustomizeCourseExam> traCustomizeCourseExamList = new ArrayList<>();
            for (Long courseId : traCourseIdList) {
                TraCustomizeCourseExam courseExam = new TraCustomizeCourseExam();
                courseExam.setExamId(examId);
                courseExam.setCourseId(courseId);
                traCustomizeCourseExamList.add(courseExam);
            }
            if (traCustomizeCourseExamMapper.insertBatch(traCustomizeCourseExamList) == 0)
                throw new RuntimeException("考试关联课程失败");
        }
        // 插入考试-用户信息
        if (CollUtil.isNotEmpty(userIdList)) {
            List<TraCustomizeExamUser> traCustomizeExamUserList = new ArrayList<>();
            for (Long userId : userIdList) {
                TraCustomizeExamUser examUser = new TraCustomizeExamUser();
                examUser.setExamId(examId);
                examUser.setUserId(userId);
                traCustomizeExamUserList.add(examUser);
            }
            if (traCustomizeExamUserMapper.insertBatch(traCustomizeExamUserList) == 0)
                throw new RuntimeException("考试关联用户失败");
        }
    }
}
