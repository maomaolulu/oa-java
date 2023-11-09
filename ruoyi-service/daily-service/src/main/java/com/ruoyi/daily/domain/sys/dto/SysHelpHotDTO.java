package com.ruoyi.daily.domain.sys.dto;

import com.ruoyi.daily.domain.sys.dto.group.Update;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by WuYang on 2022/8/19 9:21
 */
@Data
public class SysHelpHotDTO {
    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Long id;
    /**
     * 回答
     */
    private String answer;
    /**
     * 提问
     */
    private String question;
}
