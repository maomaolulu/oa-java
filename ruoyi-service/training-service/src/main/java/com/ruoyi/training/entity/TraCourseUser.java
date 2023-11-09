package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;


/**
 * 我的课程 实体表
 *
 * @author hjy
 */

public class TraCourseUser extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 课程进度（已看进度)
     */
    private String schedule;
    /**
     * 考核年度
     */
    private Integer trainYear;

    /**
     * 课程完成时间（当课程状态为2的时候）
     */
    private String finishTime;
    /**
     * 课程状态 0：未开始；1：进行中  2：已完成
     */
    private Integer status;
    /**
     * 课程加入类型 0：手动添加； 1：自定义考试时添加(不可删除)
     */
    private Integer joinType;

    //************关联信息*************
    /**
     * 培训类型id
     */
    private Long trainId;
    /**
     * 课程信息
     */
    private TraCourseInfo courseInfo;
    /**
     * 封面图片链接
     */
    private String coverUrl;
    /**
     * 部门id-载体数据，关联查询使用
     */
    private Long deptId;

    public TraCourseUser() {
    }

    public TraCourseUser(Long userId, Integer trainYear, Long trainId) {
        this.userId = userId;
        this.trainYear = trainYear;
        this.trainId = trainId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
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

    public TraCourseInfo getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(TraCourseInfo courseInfo) {
        this.courseInfo = courseInfo;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }
}
