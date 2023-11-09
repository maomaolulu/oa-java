package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.training.entity.vo.CourseUserVO;

import java.math.BigDecimal;
import java.util.List;


/**
 * 我的考试
 *
 * @author hjy
 */
public class TraMyExam extends BaseEntity {
    /**
     * 主键  自增
     */
    private Long id;
    /**
     * 考试类型：0：每年默认考试；1自定义考试
     */
    private Integer examType;
    /**
     * 考试名称
     */
    private String examName;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 已完成学时（通过计算获取）
     */
    private BigDecimal fulfilHour;
    /**
     * 考核年度
     */
    private Integer trainYear;

    /**
     * 培训类型id
     */
    private Long trainId;
    /**
     * 关联信息-培训类型名称
     */
    private String categoryName;

    /**
     * 考试状态  0：未开始   1：未通过  2：已通过
     */
    private Integer status;

    /**
     * 考试分数(最新一次)
     */
    private BigDecimal score;

    /**
     * 关联数据-是否可以考试  0：不可考试  1：可以考试
     * 注：此属性用于判断是否满足考试的条件
     * hjy
     */
    private Integer checked;
    /**
     * 关联数据-考试中我的课程类别标签
     */
    private String categoryList;
    /**
     * 存放已学课程-数据载体
     */
    private List<CourseUserVO> courseList;

    /**
     * 考试信息id
     */
    private Long examId;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public TraMyExam() {
    }

    public TraMyExam(Long userId, Integer trainYear, Long trainId) {
        this.userId = userId;
        this.trainYear = trainYear;
        this.trainId = trainId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getFulfilHour() {
        return fulfilHour;
    }

    public void setFulfilHour(BigDecimal fulfilHour) {
        this.fulfilHour = fulfilHour;
    }

    public Integer getTrainYear() {
        return trainYear;
    }

    public void setTrainYear(Integer trainYear) {
        this.trainYear = trainYear;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(String categoryList) {
        this.categoryList = categoryList;
    }

    public List<CourseUserVO> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseUserVO> courseList) {
        this.courseList = courseList;
    }
}
