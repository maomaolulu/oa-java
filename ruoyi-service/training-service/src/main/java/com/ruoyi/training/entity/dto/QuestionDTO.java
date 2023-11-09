package com.ruoyi.training.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @Author: zx
 * @CreateTime: 2022-06-01  21:00
 * @Description: 试题DTO
 *
 */
@Data
@Accessors(chain = true)
public class QuestionDTO {
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
     * 课程id
     */
    private BigInteger courseId;
    /**
     * 课程类别
     */
    private String categoryName;
    /**
     * 课程类别id
     */
    private BigInteger categoryId;
    /**
     * 最后操作者
     */
    private String updateBy;
    /**
     * 最后操作时间
     */
    private String updateTime;
    /**
     * 题型
     */
    private Integer type;

    /**
     * 公司id
     */
    private Long companyId;
}
