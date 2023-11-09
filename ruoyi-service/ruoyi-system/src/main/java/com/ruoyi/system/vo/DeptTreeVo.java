package com.ruoyi.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author zx
 * @date 2022/2/18 15:58
 */
@Data
@AllArgsConstructor
public class DeptTreeVo {
    private Long value;
    private String title;
    private List<DeptTreeVo> children;
    private Long deptId;
    private Long parentId;
}
