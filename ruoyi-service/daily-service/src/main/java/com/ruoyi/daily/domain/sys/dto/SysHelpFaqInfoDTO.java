package com.ruoyi.daily.domain.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by WuYang on 2022/8/19 11:37
 */
@Data
public class SysHelpFaqInfoDTO {
    /**
     * fid(联表)
     */
    @NotNull(message = "关联id不能为空")
    private Long fid;
    /**
     * 回答
     */
    private String answer;
    /**
     * 提问
     */
    private String question;
}
