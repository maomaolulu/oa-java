package com.ruoyi.daily.domain.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by WuYang on 2022/8/19 10:26
 */
@Data
public class SysHelpFaqAddDTO {

    private String answer;
    private String question;

}
