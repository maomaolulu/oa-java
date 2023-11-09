package com.ruoyi.activiti.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/4/20 19:09
 * @Version 1.0
 * @Description
 */
@Data
public class BizApplyLimitVO implements Serializable {

    private final static long serialVersionUID = 1L;

    /** 部门id列表 **/
    private List<Long> deptIdList;

    /** 角色id列表 **/
    private List<Integer> roleIdList;
}
