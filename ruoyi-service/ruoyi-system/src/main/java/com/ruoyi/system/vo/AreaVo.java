package com.ruoyi.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zx
 * @date 2022/5/17 17:53
 */
@Data
public class AreaVo {
    private List<AreaVo> children = new ArrayList<>();
    /** 编号 */
    private Integer           id;

    /** 上级编号 */
    private Integer           pid;
    /** 扩展名 */
    private String            extName;

    public AreaVo(Integer id, Integer pid, String extName) {
        this.id = id;
        this.pid = pid;
        this.extName = extName;
    }
}
