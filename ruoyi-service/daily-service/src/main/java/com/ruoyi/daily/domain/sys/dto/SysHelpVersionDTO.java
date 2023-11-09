package com.ruoyi.daily.domain.sys.dto;

import com.ruoyi.daily.domain.sys.dto.group.Update;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by WuYang on 2022/8/18 11:29
 */
@Data
public class SysHelpVersionDTO  {
    /**
     * 版本id
     */
    @NotNull(message = "id不能为空",groups = {Update.class})
    private Long id;
    /**
     * 版本号
     */
    @NotEmpty(message = "version不能为空" )
    private String version;
    /**
     * 标题
     */
    @NotEmpty(message = "title不能为空" )
    private String title;
    /**
     * 具体内容
     */
    private String items;
    /**
     * 日期
     */
    private LocalDate date;
    /**
     * 公告类型 1 web| 2 app| 3 小程序
     */
    @NotNull(message = "type不能为空" )
    private Integer type;
}
