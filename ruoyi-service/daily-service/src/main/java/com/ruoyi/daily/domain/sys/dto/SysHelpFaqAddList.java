package com.ruoyi.daily.domain.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/13 18:40
 */
@Data
public class SysHelpFaqAddList {
    private List<SysHelpFaqAddDTO> list;
    private String title;
    private Long id;
}
