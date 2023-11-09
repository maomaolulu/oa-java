package com.ruoyi.training.service;


import com.ruoyi.common.core.domain.R;
import com.ruoyi.training.entity.TraErrQuestionRecord;
import com.ruoyi.training.entity.TraQuestionInfo;
import com.ruoyi.training.entity.dto.QuestionDTO;
import com.ruoyi.training.entity.dto.SubmitDTO;
import com.ruoyi.training.entity.vo.QuestionVO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 题目表
 *
 * @author : zx
 * @date : 2022-5-30
 */
public interface TraQuestionInfoService {
    /**
     * 新增题目
     *
     * @param traQuestionInfo 题目信息
     */
    void insert(TraQuestionInfo traQuestionInfo);

    /**
     * 编辑题目
     *
     * @param traQuestionInfo 题目信息
     */
    void update(TraQuestionInfo traQuestionInfo);

    /**
     * 删除题目
     * @param id 题目id
     */
    void delete(BigInteger id);

    /**
     * 查询题目
     * @param questionDto 查询条件
     * @return
     */
    R getList(QuestionDTO questionDto);

    /**
     * 查询题目详情
     * @param id
     * @return
     */
    QuestionVO getById(BigInteger id);

    /**
     * 根据课程id生成试卷
     * @return
     */
    Map<String, Object> generate(Long examId);

    /**
     * 根据课程id生成试卷(宁波)
     * @return
     */
    Map<String, Object> generateNingBo(Long examId);

    /**
     * 自定义试卷
     * @return
     */
    Map<String, Object> generateCustom(Long examId);

    /**
     * 提交试卷
     * @param traQuestionInfo
     * @return
     */
    Map<String,Object> submit(SubmitDTO traQuestionInfo);
    /**
     * 提交自定义试卷
     * @param traQuestionInfo
     * @return
     */
    Map<String,Object> submitCustom(SubmitDTO traQuestionInfo);

    /**
     * 查看错题
     * @return
     */
    List<TraErrQuestionRecord> getErrList(Long examId);

    /**
     * 获取视频上次观看记录
     * @param courseId 课程id
     * @param md5 视频唯一识别号
     * @return
     */
    Map<String,Object> getVideoLog(String courseId,String md5);

    /**
     * 保存视频观看记录
     * @param courseId 课程id
     * @param log 观看日志
     */
    void saveVideoLog(String courseId, String log);
}