package com.ruoyi.activiti.domain.dto;

import com.ruoyi.activiti.domain.fiance.BizReviewInfo;
import lombok.Data;

import java.util.List;

/**
 * @author zx
 * @date 2022/4/2 13:43
 */
@Data
public class ReviewInfoEditDto {
    private List<BizReviewInfo> bizReviewInfos;
    private String taskId;
}
