package com.ruoyi.activiti.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/4/20 15:02
 * @Version 1.0
 * @Description
 */
@Data
public class BizApplyLimitDTO implements Serializable {

    private final static long serialVersionUID = 1L;

    /** 费用类别processKey列表 **/
    private List<String> processKeyList;

    /** 部门id列表 **/
    private List<Long> deptIdList;

    /** 角色id列表 **/
    private List<Integer> roleIdList;
}
