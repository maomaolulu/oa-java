package com.ruoyi.training.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: zx
 * @CreateTime: 2022-06-07  11:25
 * @Description: 提交试卷DTO
 */
@Data
public class SubmitDTO {
    /**
     * 课程id （考试id）
     */
    private Long examId;
    /**
     * 答题结果
     */
    private List<Map<String,Object>> answers;

}
