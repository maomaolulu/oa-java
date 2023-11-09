package com.ruoyi.training.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author: zx
 * @CreateTime: 2022-06-01  21:00
 * @Description: 试题BO
 *
 */
@Data
public class QuestionBO {
    /**
     * 试题id
     */
    private BigInteger id;
    /**
     * 试题内容
     */
    private String content;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 课程类别
     */
    private String categoryName;
    /**
     * 最后操作者
     */
    private String updateBy;
    /**
     * 最后操作时间
     */
    private String updateTime;
    /**
     * 答案
     */
    private String answer;
    /**
     * 分值
     */
    private Integer score;
    /**
     * 题型
     */
    private Integer type;

    /**
     * 部门id（公司id）
     */
    private BigInteger deptId;
    /**
     * 部门名称（公司名称）
     */
    private String deptName;
}
