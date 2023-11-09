package com.ruoyi.daily.domain.sys.dto;

import lombok.Data;

import java.util.List;
/**
 * 权限管理
 * @author zx
 * @date 2022-10-21 17:39:27
 */
@Data
public class SysUserAuthDto {
    private Long userId;
    private Long roleId;
    private List<Long> deptIdList;
}
