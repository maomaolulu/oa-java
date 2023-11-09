package com.ruoyi.training.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.file.domain.Res;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.training.entity.*;
import com.ruoyi.training.entity.bo.QuestionBO;
import com.ruoyi.training.entity.dto.QuestionDTO;
import com.ruoyi.training.entity.dto.SubmitDTO;
import com.ruoyi.training.entity.vo.CourseUserVO;
import com.ruoyi.training.entity.vo.CourseVO;
import com.ruoyi.training.entity.vo.QuestionVO;
import com.ruoyi.training.entity.vo.TraCustomizeExamsVO;
import com.ruoyi.training.mapper.TraErrQuestionRecordMapper;
import com.ruoyi.training.mapper.TraOptionsInfoMapper;
import com.ruoyi.training.mapper.TraQuestionInfoMapper;
import com.ruoyi.training.mapper.TraScoreRecordMapper;
import com.ruoyi.training.service.ITraCustomizeExamService;
import com.ruoyi.training.service.ITraMyExamService;
import com.ruoyi.training.service.TraQuestionInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 题目表
 *
 * @author : zx
 * @date : 2022-5-30
 */
@Component
@Service
public class TraQuestionInfoServiceImpl implements TraQuestionInfoService {
    private final TraQuestionInfoMapper traQuestionInfoMapper;
    private final TraOptionsInfoMapper traOptionsInfoMapper;
    private final RemoteFileService remoteFileService;
    private final TraScoreRecordMapper scoreRecordMapper;
    private final TraErrQuestionRecordMapper errQuestionRecordMapper;
    private final ITraMyExamService myExamService;
    private final RemoteUserService userService;
    private final ITraCustomizeExamService customizeExamService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public TraQuestionInfoServiceImpl(TraQuestionInfoMapper traQuestionInfoMapper, TraOptionsInfoMapper traOptionsInfoMapper, RemoteFileService remoteFileService, TraScoreRecordMapper scoreRecordMapper, TraErrQuestionRecordMapper errQuestionRecordMapper, ITraMyExamService myExamService, RemoteUserService userService, DataScopeUtil dataScopeUtil, ITraCustomizeExamService customizeExamService) {
        this.traQuestionInfoMapper = traQuestionInfoMapper;
        this.traOptionsInfoMapper = traOptionsInfoMapper;
        this.remoteFileService = remoteFileService;
        this.scoreRecordMapper = scoreRecordMapper;
        this.errQuestionRecordMapper = errQuestionRecordMapper;
        this.myExamService = myExamService;
        this.userService = userService;
        this.customizeExamService = customizeExamService;
    }

    @Value("${training_score.company115}")
    private int examScore;

    private static final int ERR_CODE = 500;
    private static final String QUESTION_BANK = "question-bank";
    private static final String PARENT_ID = "parent_id";
    private static final int SINGLE_Q = 1;
    private static final int MULTI_Q = 2;
    private static final int JUDGE_Q = 3;
    private static final String ANSWER_STR = "answer";
    private static final String SCORE_STR = "score";
    /**
     * 单选数量
     */
    private static final int SINGLE_NUM = 30;
    /**
     * 多选数量
     */
    private static final int MULTI_NUM = 15;
    /**
     * 判断数量
     */
    private static final int JUDGE_NUM = 10;

    /**
     * 新增题目
     *
     * @param traQuestionInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(TraQuestionInfo traQuestionInfo) {
        try {
            traQuestionInfo.setCreateTime(new Date());
            traQuestionInfo.setCreateBy(SystemUtil.getUserNameCn());
            traQuestionInfo.setUpdateTime(new Date());
            traQuestionInfo.setUpdateBy(SystemUtil.getUserNameCn());
            traQuestionInfo.setDelFlag("0");
            traQuestionInfoMapper.insert(traQuestionInfo);
            if (traQuestionInfo.getOptionsInfos().isEmpty() && traQuestionInfo.getType() != JUDGE_Q) {
                throw new StatefulException("选项不能为空");
            }
            for (TraOptionsInfo optionsInfo : traQuestionInfo.getOptionsInfos()) {
                optionsInfo.setCreateTime(new Date());
                optionsInfo.setCreateBy(SystemUtil.getUserNameCn());
                optionsInfo.setParentId(traQuestionInfo.getId());
                traOptionsInfoMapper.insert(optionsInfo);

            }
            if (traQuestionInfo.getCourseIds().isEmpty()) {
                throw new StatefulException("关联课程不能为空");
            }
            for (BigInteger courseId : traQuestionInfo.getCourseIds()) {
                saveCq(traQuestionInfo, courseId);
            }
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(traQuestionInfo.getId().longValue(), traQuestionInfo.getUuid());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    private void saveCq(TraQuestionInfo traQuestionInfo, BigInteger courseId) {
        int result = traQuestionInfoMapper.insertCourseTraQuestion(courseId, traQuestionInfo.getId());
        if (result == 0) {
            throw new StatefulException("关联课程保存失败");
        }
    }

    /**
     * 编辑题目
     *
     * @param traQuestionInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TraQuestionInfo traQuestionInfo) {
        try {
            TraQuestionInfo oldQuestion = traQuestionInfoMapper.selectById(traQuestionInfo.getId());
            traQuestionInfo.setUpdateTime(new Date());
            traQuestionInfo.setUpdateBy(SystemUtil.getUserNameCn());
            traQuestionInfoMapper.updateById(traQuestionInfo);
            if (traQuestionInfo.getOptionsInfos().isEmpty() && traQuestionInfo.getType() != JUDGE_Q) {
                throw new StatefulException("选项不能为空");
            }
            Map<String, Object> map = new HashMap<>(1);
            map.put(PARENT_ID, traQuestionInfo.getId());
            List<TraOptionsInfo> oldOptions = traOptionsInfoMapper.selectByMap(map);
            if (oldOptions.isEmpty() && !oldQuestion.getType().equals(JUDGE_Q)) {
                throw new StatefulException("原选项为空");
            }
            if (!oldQuestion.getType().equals(JUDGE_Q)) {

                List<TraOptionsInfo> newOptions = traQuestionInfo.getOptionsInfos();

                List<TraOptionsInfo> updateOptions = new ArrayList<>();

                for (TraOptionsInfo oldOption : oldOptions) {
                    for (TraOptionsInfo newOption : newOptions) {
                        if (oldOption.getId().equals(newOption.getId())) {
                            updateOptions.add(newOption);
                        }
                    }
                }
                for (TraOptionsInfo updateOption : updateOptions) {
                    traOptionsInfoMapper.updateById(updateOption);
                }
                oldOptions.removeAll(updateOptions);
                for (TraOptionsInfo oldOption : oldOptions) {
                    traOptionsInfoMapper.deleteById(oldOption);
                }
                for (TraOptionsInfo newOption : newOptions) {
                    if (null == newOption.getId()) {
                        traOptionsInfoMapper.insert(newOption);
                    }
                }
            }
            if (traQuestionInfo.getCourseIds().isEmpty()) {
                throw new StatefulException("关联课程不能为空");
            }
            // 先删除后新增
            traQuestionInfoMapper.deleteCourse(traQuestionInfo.getId());
            for (BigInteger courseId : traQuestionInfo.getCourseIds()) {
                saveCq(traQuestionInfo, courseId);
            }

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(traQuestionInfo.getId().longValue(), traQuestionInfo.getUuid());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 删除题目
     *
     * @param id 题目id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {
        try {
            // 删除题目
            traQuestionInfoMapper.deleteById(id);
            // 删除课程关联
            traQuestionInfoMapper.deleteCourse(id);
            // 删除选项
            traOptionsInfoMapper.delete(new QueryWrapper<TraOptionsInfo>().eq(PARENT_ID, id));
            // 删除图片
            List<SysAttachment> list = remoteFileService.getList(id.longValue(), QUESTION_BANK);
            list.stream().forEach(sysAttachment -> {
                Res delete = remoteFileService.delete(QUESTION_BANK, sysAttachment.getUrl());
                if (delete.getCode() == ERR_CODE) {
                    throw new StatefulException("删除题目图片失败 id:{}" + id);
                }
            });
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 查询题目
     *
     * @param questionDto 查询条件
     * @return
     */
    @Override
    public R getList(QuestionDTO questionDto) {
        List<QuestionBO> questionList = getQuestionBos(questionDto);
        PageInfo<?> pageInfo = new PageInfo(questionList);
        Map<String, Object> m = new HashMap<String, Object>(5);

        m.put("currPage", pageInfo.getPageNum());
        m.put("pageSize", pageInfo.getPageSize());
        m.put("totalCount", pageInfo.getTotal());
        m.put("totalPage", pageInfo.getPages());
        List<QuestionVO> result = questionList.stream().collect(groupingBy(QuestionBO::getId))
                .entrySet()
                .stream().map(e -> {
                    List<CourseVO> courseVOList = new ArrayList<>();
                    if (StrUtil.isNotBlank(e.getValue().get(0).getCourseName())) {
                        Arrays.stream(e.getValue().get(0).getCourseName().split(";")).forEach(course -> {
                                    courseVOList.add(new CourseVO().setCourseName(course).setCategoryName(e.getValue().get(0).getCategoryName()));
                                }
                        );
                    }
                    QueryWrapper<TraOptionsInfo> wrapper = new QueryWrapper<>();
                    wrapper.eq("parent_id", e.getKey());
                    List<TraOptionsInfo> traOptionsInfos = traOptionsInfoMapper.selectList(wrapper);
                    List<SysAttachment> list = remoteFileService.getList(e.getKey().longValue(), QUESTION_BANK);
                    return new QuestionVO().setId(e.getKey())
                            .setCourseInfo(courseVOList)
                            .setOptionInfos(traOptionsInfos)
                            .setImgList(list)
                            .setType(e.getValue().get(0).getType())
                            .setAnswer(e.getValue().get(0).getAnswer())
                            .setCategoryName(e.getValue().get(0).getCategoryName())
                            .setContent(e.getValue().get(0).getContent())
                            .setUpdateBy(e.getValue().get(0).getUpdateBy())
                            .setUpdateTime(e.getValue().get(0).getUpdateTime())
                            .setDeptId(e.getValue().get(0).getDeptId())
                            .setDeptName(e.getValue().get(0).getDeptName());
                }).sorted(Comparator.comparing(QuestionVO::getUpdateTime).reversed()).collect(Collectors.toList());
        m.put("list", result);
        return R.data(m);
    }

    private List<QuestionBO> getQuestionBos(QuestionDTO questionDto) {
        SysUser user = userService.selectSysUserByUserId(SystemUtil.getUserId());
        QueryWrapper<QuestionDTO> wrapper = new QueryWrapper<>();
        Long companyId = questionDto.getCompanyId();
        if (companyId != null) {
            // 按照前端传过来的公司id查询
            wrapper.eq("c.company_id", companyId);
        } else {
            // 按数据权限查询
            String userPermission = user.getUserPermission();
            if (StrUtil.isBlank(userPermission)) {
                wrapper.eq("c.company_id", user.getCompanyId());
            } else {
                wrapper.in("c.company_id", Arrays.asList(userPermission.split(";")));
            }
        }
        // 按照课程类别查询
        wrapper.eq(StrUtil.isNotBlank(questionDto.getCategoryName()),"c.category_name",questionDto.getCategoryName());
        // 按照课程名称查询
        wrapper.eq(StrUtil.isNotBlank(questionDto.getCourseName()),"b.course_name",questionDto.getCourseName());

        wrapper.eq("a.del_flag", "0");
        wrapper.eq(questionDto.getId() != null, "a.id", questionDto.getId());
        // 课程id
        wrapper.eq(null != questionDto.getCourseId(), "b.id", questionDto.getCourseId());
        // 类别id
        wrapper.eq(null != questionDto.getCategoryId(), "c.id", questionDto.getCategoryId());
        // 题目类型
        wrapper.eq(null != questionDto.getType(), "a.type", questionDto.getType());
        // 最后操作者
        wrapper.like(CharSequenceUtil.isNotBlank(questionDto.getUpdateBy()), "a.update_by", questionDto.getUpdateBy());
        wrapper.groupBy(" a.id,c.id ");
        wrapper.orderByDesc("a.update_time");

        List<QuestionBO> questionList = traQuestionInfoMapper.questionList(wrapper);
        return questionList;
    }

    /**
     * 查询题目详情
     *
     * @param id
     * @return
     */
    @Override
    public QuestionVO getById(BigInteger id) {
        QuestionVO questionVO = new QuestionVO();
        // 获取题目信息
        TraQuestionInfo traQuestionInfo = traQuestionInfoMapper.selectById(id);
        if (null == traQuestionInfo) {
            throw new StatefulException("题目不存在");
        }
        BeanUtils.copyProperties(traQuestionInfo, questionVO);
        // 获取选项
        if (SINGLE_Q == traQuestionInfo.getType() || MULTI_Q == traQuestionInfo.getType()) {
            QueryWrapper<TraOptionsInfo> wrapper = new QueryWrapper<>();
            wrapper.eq(PARENT_ID, id);
            wrapper.orderByAsc("sort_field");
            List<TraOptionsInfo> traOptionsInfos = traOptionsInfoMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(traOptionsInfos)) {
                throw new StatefulException("题目选项不存在");
            }
            questionVO.setOptionInfos(traOptionsInfos);
        }
        // 获取题目图片
        List<SysAttachment> list = remoteFileService.getList(id.longValue(), QUESTION_BANK);
        questionVO.setImgList(list);
        questionVO.setAnswer(null);
        return questionVO;
    }

    private BigDecimal getQuestionNum(BigDecimal oneClass, BigDecimal allClass, BigDecimal num) {
        System.out.println(oneClass.toString());
        System.out.println(allClass.toString());
        System.out.println(num.toString());
        BigDecimal a = oneClass.divide(allClass, 2, BigDecimal.ROUND_HALF_UP).multiply(num).setScale(0, RoundingMode.HALF_UP);
        return a;
    }


    /**
     * 根据考试id生成试卷
     *
     * @return
     */
    @Override
    public Map<String, Object> generate(Long examId) {

        // 获取课程下的题目
        Map<String, Object> map = new HashMap<>(3);
        String paperInfo = redisUtils.get("paper_info:" + SystemUtil.getUserId() + ":" + examId);
        String paperAnswer = redisUtils.get("paper_answer:" + SystemUtil.getUserId() + ":" + examId);

        if (paperInfo != null && paperAnswer != null) {
            return JSON.parseObject(paperInfo);
        }
        // 获取用户课程信息
        TraMyExam traMyExam = myExamService.selectMyExamById(examId);
        List<CourseUserVO> courseList = traMyExam.getCourseList();
        BigDecimal reduce = courseList.stream().map(CourseUserVO::getCourseHour).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Map<String, Object>> singleList = new ArrayList<>();
        List<Map<String, Object>> judgeList = new ArrayList<>();
        List<Map<String, Object>> multiList = new ArrayList<>();
        Set<Map<String, Object>> answerList = new HashSet<>();
        List<String> info = new ArrayList<>();
        courseList.stream().forEach(courseUserVO -> {
            // 单选题
            int singleCourse = getQuestionNum(courseUserVO.getCourseHour(), reduce, new BigDecimal(SINGLE_NUM)).intValue();
            List<QuestionBO> single = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getCourseId())).setType(SINGLE_Q));
            if (single.size() < singleCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "单选题数量不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "单选:" + singleCourse);
            Collections.shuffle(single);
            single.subList(0, singleCourse).stream().forEach(singleQuestionVO -> {
                HashMap<String, Object> singleMap = new HashMap<>(2);
                singleMap.put("sort", singleList.size() + 1);
                singleMap.put("id", singleQuestionVO.getId());
                singleList.add(new HashMap<String, Object>(2) {{
                    putAll(singleMap);
                }});
                singleMap.put(ANSWER_STR, singleQuestionVO.getAnswer());
                singleMap.put(SCORE_STR, singleQuestionVO.getScore());
                answerList.add(singleMap);
            });
            // 多选题
            int multiCourse = getQuestionNum(courseUserVO.getCourseHour(), reduce, new BigDecimal(MULTI_NUM)).intValue();
            List<QuestionBO> multi = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getCourseId())).setType(MULTI_Q));
            if (multi.size() < multiCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "多选题数量不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "多选:" + multiCourse);
            Collections.shuffle(multi);
            multi.subList(0, multiCourse).stream().forEach(multiQuestionVO -> {
                HashMap<String, Object> multiMap = new HashMap<>(2);
                multiMap.put("sort", multiList.size() + 1);
                multiMap.put("id", multiQuestionVO.getId());
                multiList.add(new HashMap<String, Object>(2) {{
                    putAll(multiMap);
                }});
                multiMap.put(ANSWER_STR, multiQuestionVO.getAnswer());
                multiMap.put(SCORE_STR, multiQuestionVO.getScore());
                answerList.add(multiMap);
            });
            // 判断题
            int judgeCourse = getQuestionNum(courseUserVO.getCourseHour(), reduce, new BigDecimal(JUDGE_NUM)).intValue();
            List<QuestionBO> judge = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getCourseId())).setType(JUDGE_Q));
            if (judge.size() < judgeCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "判断题不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "判断:" + judgeCourse);
            Collections.shuffle(judge);
            judge.subList(0, judgeCourse).stream().forEach(judgeQuestionVO -> {
                HashMap<String, Object> judgeMap = new HashMap<>(2);
                judgeMap.put("sort", judgeList.size() + 1);
                judgeMap.put("id", judgeQuestionVO.getId());
                judgeList.add(new HashMap<String, Object>(2) {{
                    putAll(judgeMap);
                }});
                judgeMap.put(ANSWER_STR, judgeQuestionVO.getAnswer());
                judgeMap.put(SCORE_STR, judgeQuestionVO.getScore());
                answerList.add(judgeMap);
            });
        });
        info.stream().forEach(i -> System.out.println(i));

        map.put("single", singleList.subList(0, SINGLE_NUM));
        map.put("multi", multiList.subList(0, MULTI_NUM));
        map.put("judge", judgeList.subList(0, JUDGE_NUM));

        String str = JSON.toJSONString(map);
        redisUtils.set("paper_info:" + SystemUtil.getUserId() + ":" + examId, str, 60 * 91);
        List<String> al = new ArrayList<>();
        Set<Map<String, Object>> answerListResult = new HashSet<>();
        //去重
        answerList.stream().forEach(a -> {
            if (!al.contains(a.get("id").toString())) {
                answerListResult.add(a);
                al.add(a.get("id").toString());
            }
        });
        redisUtils.set("paper_answer:" + SystemUtil.getUserId() + ":" + examId, JSON.toJSONString(answerListResult), 60 * 91);

        return map;
    }

    /**
     * 根据考试id生成试卷
     *
     * @return
     */
    @Override
    public Map<String, Object> generateNingBo(Long examId) {

        // 获取课程下的题目
        Map<String, Object> map = new HashMap<>(3);
        //redis 缓存
        String paperInfo = redisUtils.get("paper_info_custom:" + SystemUtil.getUserId() + ":" + examId);
        String paperAnswer = redisUtils.get("paper_answer_custom:" + SystemUtil.getUserId() + ":" + examId);

        if (paperInfo != null && paperAnswer != null) {
            return JSON.parseObject(paperInfo);
        }
// 获取用户课程信息
        TraCustomizeExamsVO customizeExamInfo = customizeExamService.getCustomizeExamInfo(examId);
        //存放已学课程-数据载体
        List<TraCourseInfo> traCourseInfoList = customizeExamInfo.getTraCourseInfoList();
//        TraMyExam traMyExam = myExamService.selectMyExamById(examId);
//        //存放已学课程-数据载体
//        List<CourseUserVO> courseList = traMyExam.getCourseList();
        //总学时
//        BigDecimal reduce = courseList.stream().map(CourseUserVO::getCourseHour).reduce(BigDecimal.ZERO, BigDecimal::add);
        //单选
        List<Map<String, Object>> singleList = new ArrayList<>();
        //判断
        List<Map<String, Object>> judgeList = new ArrayList<>();
        //多选
        List<Map<String, Object>> multiList = new ArrayList<>();
        //答案
        Set<Map<String, Object>> answerList = new HashSet<>();

        List<String> info = new ArrayList<>();

        //课程id去重   取全部题目  根据size计算分数
        List<Long> courseIds = traCourseInfoList.stream().distinct().map(TraCourseInfo::getId).collect(Collectors.toList());
        //单选
        List<QuestionBO> single = getQuestionBosNingBo(courseIds,SINGLE_Q);
        //判断
        List<QuestionBO> judge = getQuestionBosNingBo(courseIds,JUDGE_Q);
        //多选
        List<QuestionBO> multi = getQuestionBosNingBo(courseIds,MULTI_Q);

        Integer i2 = single.size() + judge.size() + multi.size();
        double v = i2.doubleValue();
        //平均分数
        double score = 100 / v;

        //单选
        for (int i = 0; i < single.size(); i++) {
            HashMap<String, Object> singleMap = new HashMap<>(2);
            singleMap.put("sort", i+1);
            singleMap.put("id", single.get(i).getId());
            singleList.add(new HashMap<String, Object>(2) {{
                putAll(singleMap);
            }});
            //答案
            singleMap.put(ANSWER_STR, single.get(i).getAnswer());
            //分数
            singleMap.put(SCORE_STR, score);
            answerList.add(singleMap);
        }
        //多选
        for (int i = 0; i <  multi.size(); i++) {
            HashMap<String, Object> multiMap = new HashMap<>(2);
            multiMap.put("sort", i+1);
            multiMap.put("id", multi.get(i).getId());
            multiList.add(new HashMap<String, Object>(2) {{
                putAll(multiMap);
            }});
            //答案
            multiMap.put(ANSWER_STR, multi.get(i).getAnswer());
            //分数
            multiMap.put(SCORE_STR, score);
            answerList.add(multiMap);
        }
        //判断
        for (int i = 0; i < judge.size(); i++) {
            HashMap<String, Object> judgeMap = new HashMap<>(2);
            judgeMap.put("sort", i+1);
            judgeMap.put("id", judge.get(i).getId());
            judgeList.add(new HashMap<String, Object>(2) {{
                putAll(judgeMap);
            }});
            //答案
            judgeMap.put(ANSWER_STR, judge.get(i).getAnswer());
            //分数
            judgeMap.put(SCORE_STR, score);
            answerList.add(judgeMap);
        }

        map.put("single", singleList);
        map.put("multi", multiList);
        map.put("judge", judgeList);

        String str = JSON.toJSONString(map);
        redisUtils.set("paper_info:" + SystemUtil.getUserId() + ":" + examId, str, 60 * 91);
        List<String> al = new ArrayList<>();
        Set<Map<String, Object>> answerListResult = new HashSet<>();
        //去重
        answerList.stream().forEach(a -> {
            if (!al.contains(a.get("id").toString())) {
                answerListResult.add(a);
                al.add(a.get("id").toString());
            }
        });
        redisUtils.set("paper_answer_custom:" + SystemUtil.getUserId() + ":" + examId, JSON.toJSONString(answerListResult), 60 * 91);

        return map;
    }


    private List<QuestionBO> getQuestionBosNingBo(List<Long> courseIds,Integer type ) {
        SysUser user = userService.selectSysUserByUserId(SystemUtil.getUserId());
        QueryWrapper<QuestionDTO> wrapper = new QueryWrapper<>();

            // 按数据权限查询
            String userPermission = user.getUserPermission();
            if (StrUtil.isBlank(userPermission)) {
                wrapper.eq("c.company_id", user.getCompanyId());
            } else {
                wrapper.in("c.company_id", Arrays.asList(userPermission.split(";")));
            }

        // 课程id
        wrapper.in( "b.id", courseIds);
        wrapper.eq("a.type", type);
        wrapper.groupBy(" a.id,c.id ");
        wrapper.orderByDesc("a.update_time");

        List<QuestionBO> questionList = traQuestionInfoMapper.questionList(wrapper);
        return questionList;
    }
    /**
     * 自定义试卷
     *
     * @param examId
     * @return
     */
    @Override
    public Map<String, Object> generateCustom(Long examId) {
        // 获取课程下的题目
        Map<String, Object> map = new HashMap<>(3);
        String paperInfo = redisUtils.get("paper_info_custom:" + SystemUtil.getUserId() + ":" + examId);
        String paperAnswer = redisUtils.get("paper_answer_custom:" + SystemUtil.getUserId() + ":" + examId);

        if (paperInfo != null && paperAnswer != null) {
            return JSON.parseObject(paperInfo);
        }
        // 获取用户课程信息
        TraCustomizeExamsVO customizeExamInfo = customizeExamService.getCustomizeExamInfo(examId);
        List<TraCourseInfo> courseList = customizeExamInfo.getTraCourseInfoList();
        BigDecimal reduce = courseList.stream().map(TraCourseInfo::getClassHour).reduce(BigDecimal.ZERO, BigDecimal::add);
        TraCustomizeExam traCustomizeExam = customizeExamInfo.getTraCustomizeExam();
        List<Map<String, Object>> singleList = new ArrayList<>();
        List<Map<String, Object>> judgeList = new ArrayList<>();
        List<Map<String, Object>> multiList = new ArrayList<>();
        Set<Map<String, Object>> answerList = new HashSet<>();
        List<String> info = new ArrayList<>();
        courseList.stream().forEach(courseUserVO -> {
            // 单选题
            int singleCourse = getQuestionNum(courseUserVO.getClassHour(), reduce, new BigDecimal(traCustomizeExam.getSingleNumber())).intValue();
            List<QuestionBO> single = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getId())).setType(SINGLE_Q));
            if (single.size() < singleCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "单选题数量不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "单选:" + singleCourse);
            Collections.shuffle(single);
            single.subList(0, singleCourse).stream().forEach(singleQuestionVO -> {
                HashMap<String, Object> singleMap = new HashMap<>(2);
                singleMap.put("sort", singleList.size() + 1);
                singleMap.put("id", singleQuestionVO.getId());
                singleList.add(new HashMap<String, Object>(2) {{
                    putAll(singleMap);
                }});
                singleMap.put(ANSWER_STR, singleQuestionVO.getAnswer());
                singleMap.put(SCORE_STR, traCustomizeExam.getSingleScore().intValue());
                answerList.add(singleMap);
            });
            // 多选题
            int multiCourse = getQuestionNum(courseUserVO.getClassHour(), reduce, new BigDecimal(traCustomizeExam.getMultiNumber())).intValue();
            List<QuestionBO> multi = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getId())).setType(MULTI_Q));
            if (multi.size() < multiCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "多选题数量不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "多选:" + multiCourse);
            Collections.shuffle(multi);
            multi.subList(0, multiCourse).stream().forEach(multiQuestionVO -> {
                HashMap<String, Object> multiMap = new HashMap<>(2);
                multiMap.put("sort", multiList.size() + 1);
                multiMap.put("id", multiQuestionVO.getId());
                multiList.add(new HashMap<String, Object>(2) {{
                    putAll(multiMap);
                }});
                multiMap.put(ANSWER_STR, multiQuestionVO.getAnswer());
                multiMap.put(SCORE_STR, traCustomizeExam.getMultiScore().intValue());
                answerList.add(multiMap);
            });
            // 判断题
            int judgeCourse = getQuestionNum(courseUserVO.getClassHour(), reduce, new BigDecimal(traCustomizeExam.getJudgeNumber())).intValue();
            List<QuestionBO> judge = getQuestionBos(new QuestionDTO().setCourseId(BigInteger.valueOf(courseUserVO.getId())).setType(JUDGE_Q));
            if (judge.size() < judgeCourse) {
                throw new StatefulException("题库课程-" + courseUserVO.getCourseName() + "判断题不足");
            }
            info.add("题库课程-" + courseUserVO.getCourseName() + "判断:" + judgeCourse);
            Collections.shuffle(judge);
            judge.subList(0, judgeCourse).stream().forEach(judgeQuestionVO -> {
                HashMap<String, Object> judgeMap = new HashMap<>(2);
                judgeMap.put("sort", judgeList.size() + 1);
                judgeMap.put("id", judgeQuestionVO.getId());
                judgeList.add(new HashMap<String, Object>(2) {{
                    putAll(judgeMap);
                }});
                judgeMap.put(ANSWER_STR, judgeQuestionVO.getAnswer());
                judgeMap.put(SCORE_STR, traCustomizeExam.getJudgeScore().intValue());
                answerList.add(judgeMap);
            });
        });
        info.stream().forEach(i -> System.out.println(i));

        map.put("single", singleList.subList(0, traCustomizeExam.getSingleNumber().intValue()));
        map.put("multi", multiList.subList(0, traCustomizeExam.getMultiNumber().intValue()));
        map.put("judge", judgeList.subList(0, traCustomizeExam.getJudgeNumber().intValue()));

        String str = JSON.toJSONString(map);
        redisUtils.set("paper_info_custom:" + SystemUtil.getUserId() + ":" + examId, str, 60 * 91);
        List<String> al = new ArrayList<>();
        Set<Map<String, Object>> answerListResult = new HashSet<>();
        answerList.stream().forEach(a -> {
            if (!al.contains(a.get("id").toString())) {
                answerListResult.add(a);
                al.add(a.get("id").toString());
            }
        });
        redisUtils.set("paper_answer_custom:" + SystemUtil.getUserId() + ":" + examId, JSON.toJSONString(answerListResult), 60 * 91);

        return map;
    }

    /**
     * 提交试卷
     *
     * @param submitDTO
     * @return
     */
    @Override
    public Map<String, Object> submit(SubmitDTO submitDTO) {
//        if(true){
//            return R.ok();
//        }
        String answerStr = redisUtils.get("paper_answer:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        List<Map<String, Object>> answerList = JSONArray.parseObject(answerStr, List.class);
        List<Map<String, Object>> answers = submitDTO.getAnswers();
        List<Double> score = new ArrayList<>();
        answers.stream().forEach(a -> System.out.println(a.get("id").toString() + "-****-" + a.get("answer").toString()));
        List<TraErrQuestionRecord> errList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        answerList.stream().forEach(answerMap -> {
            answers.stream().forEach(answer -> {
                if (answer.get("id").toString().equals(answerMap.get("id").toString())) {
                    String[] splitReal = answerMap.get(ANSWER_STR).toString().split(";");
                    String[] splitUser = answer.get("answer").toString().split(";");
                    Arrays.sort(splitReal);
                    Arrays.sort(splitUser);
                    if (Arrays.equals(splitReal, splitUser)) {
//                        if(set.contains(answer.get("id").toString())){
//                            return;
//                        }
                        score.add(Double.parseDouble(answerMap.get(SCORE_STR).toString()));
                        set.add(answer.get("id").toString());
                        System.out.println(answerMap.get("id") + ":" + answerMap.get(SCORE_STR).toString());
                    } else {
                        errList.add(new TraErrQuestionRecord()
                                .setQuestionId(BigInteger.valueOf(Long.valueOf(answer.get("id").toString())))
                                .setAnswer(answer.get("answer").toString())
                                .setExamId(submitDTO.getExamId())
                                .setCreateTime(new Date())
                                .setUserId(SystemUtil.getUserId()));
                    }
                }
            });
        });
        double sum = score.stream().mapToInt(Double::intValue).sum();
        scoreRecordMapper.insert(new TraScoreRecord().setExamId(submitDTO.getExamId()).setUserId(SystemUtil.getUserId().intValue())
                .setScore(new BigDecimal(sum).setScale(2,BigDecimal.ROUND_HALF_UP)).setUserName(SystemUtil.getUserNameCn()).setCreateBy(SystemUtil.getUserId().toString())
                .setCreateTime(new Date()));
        redisUtils.delete("paper_info:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        redisUtils.delete("paper_answer:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        Map<String, Object> result = new HashMap<>(2);
        result.put("score", sum);
        int status = 0;
        if (sum < examScore) {
            // 0不通过
            result.put("level", 0);
            errList.stream().forEach(err -> errQuestionRecordMapper.insert(err));
            status = 1;
        } else {
            // 合格删除本人课程错题记录
            result.put("level", 1);
            QueryWrapper<TraErrQuestionRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("exam_id", submitDTO.getExamId());
            wrapper.eq("user_id", SystemUtil.getUserId());
            errQuestionRecordMapper.delete(wrapper);
            status = 2;
        }
        scoreRecordMapper.updateExamInfo(status, sum, submitDTO.getExamId());
        result.put("status", status);
        return result;
    }

    /**
     * 提交自定义试卷
     *
     * @param submitDTO
     * @return
     */
    @Override
    public Map<String, Object> submitCustom(SubmitDTO submitDTO) {
        TraCustomizeExamsVO customizeExamInfo = customizeExamService.getCustomizeExamInfo(submitDTO.getExamId());
        TraCustomizeExam traCustomizeExam = customizeExamInfo.getTraCustomizeExam();
        String answerStr = redisUtils.get("paper_answer_custom:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        List<Map<String, Object>> answerList = JSONArray.parseObject(answerStr, List.class);
        List<Map<String, Object>> answers = submitDTO.getAnswers();
        List<Double> score = new ArrayList<>();
        answers.stream().forEach(a -> System.out.println(a.get("id").toString() + "-****-" + a.get("answer").toString()));
        List<TraErrQuestionRecord> errList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        answerList.stream().forEach(answerMap -> {
            answers.stream().forEach(answer -> {
                if (answer.get("id").toString().equals(answerMap.get("id").toString())) {
                    String[] splitReal = answerMap.get(ANSWER_STR).toString().split(";");
                    String[] splitUser = answer.get("answer").toString().split(";");
                    Arrays.sort(splitReal);
                    Arrays.sort(splitUser);
                    if (Arrays.equals(splitReal, splitUser)) {
//                        if(set.contains(answer.get("id").toString())){
//                            return;
//                        }
                        score.add(Double.parseDouble(answerMap.get(SCORE_STR).toString()));
                        set.add(answer.get("id").toString());
                        System.out.println(answerMap.get("id") + ":" + answerMap.get(SCORE_STR).toString());
                    } else {
                        errList.add(new TraErrQuestionRecord()
                                .setQuestionId(BigInteger.valueOf(Long.valueOf(answer.get("id").toString())))
                                .setAnswer(answer.get("answer").toString())
                                .setExamId(submitDTO.getExamId())
                                .setCreateTime(new Date())
                                .setUserId(SystemUtil.getUserId()));
                    }
                }
            });
        });
        double sum = score.stream().mapToDouble(Double::doubleValue).sum();

        scoreRecordMapper.insert(new TraScoreRecord().setExamId(submitDTO.getExamId()).setUserId(SystemUtil.getUserId().intValue())
                .setScore(new BigDecimal(sum).setScale(2,BigDecimal.ROUND_HALF_UP)).setUserName(SystemUtil.getUserNameCn()).setCreateBy(SystemUtil.getUserId().toString())
                .setCreateTime(new Date()));
        redisUtils.delete("paper_info_custom:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        redisUtils.delete("paper_answer_custom:" + SystemUtil.getUserId() + ":" + submitDTO.getExamId());
        Map<String, Object> result = new HashMap<>(2);
        result.put("score", new BigDecimal(sum).setScale(2,BigDecimal.ROUND_HALF_UP));
        int status = 0;
        if (sum < traCustomizeExam.getPassScore().intValue()) {
            // 0不通过
            result.put("level", 0);
            errList.stream().forEach(err -> errQuestionRecordMapper.insert(err));
            status = 1;
        } else {
            // 合格删除本人课程错题记录
            result.put("level", 1);
            QueryWrapper<TraErrQuestionRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("exam_id", submitDTO.getExamId());
            wrapper.eq("user_id", SystemUtil.getUserId());
            errQuestionRecordMapper.delete(wrapper);
            status = 2;
        }
        // TODO: 2023-02-21  修改统一考试表分数  有问题 全部修改了   修改的这个表 tra_course_exam
        scoreRecordMapper.updateExamInfoNew(status, sum, submitDTO.getExamId(),SystemUtil.getUserId());
        result.put("status", status);
        result.put("total", traCustomizeExam.getTotalScore());
        return result;
    }

    /**
     * 查看错题
     *
     * @return
     */
    @Override
    public List<TraErrQuestionRecord> getErrList(Long examId) {

        return new LambdaQueryChainWrapper<>(errQuestionRecordMapper)
                .eq(TraErrQuestionRecord::getUserId, SystemUtil.getUserId())
                .eq(TraErrQuestionRecord::getExamId, examId)
                .orderByDesc(TraErrQuestionRecord::getCreateTime).list();
    }

    /**
     * 获取视频上次观看记录
     *
     * @param courseId 课程id
     * @param md5      视频唯一识别信息
     * @return
     */
    @Override
    public Map<String, Object> getVideoLog(String courseId, String md5) {
        String historyLog = redisUtils.get("training-video" + courseId + ":" + SystemUtil.getUserId());
        Map<String, Object> map = new HashMap<>(2);
        map.put("historyLog", historyLog == null ? "0" : historyLog);
        List<SysAttachment> training = remoteFileService.getListByTempId(md5, "training");
        if (training.isEmpty()) {
            map.put("video", "");
            return map;
        }
        SysAttachment sysAttachment = training.get(0);
        map.put("video", sysAttachment.getPreUrl());
        return map;
    }

    /**
     * 保存视频观看记录
     *
     * @param courseId 课程id
     * @param log      观看日志
     */
    @Override
    public void saveVideoLog(String courseId, String log) {
        redisUtils.set("training-video" + courseId + ":" + SystemUtil.getUserId(), log, 60 * 60 * 24 * 365);
    }


}