package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraDeptHours;

import java.util.List;

/**
 * 部门学时service
 *
 * @author hjy
 * @date 2022/6/15 15:00
 */
public interface TraDeptHoursService {
    /**
     * 获取部门学时列表
     *
     * @param traDeptHours 部门学时信息
     * @return 部门学时信息列表
     */
    List<TraDeptHours> selectTraDeptHoursList(TraDeptHours traDeptHours);

    /**
     * 通过id获取部门学时详情
     *
     * @param id 部门学时id
     * @return 部门学时信息列表
     */
    TraDeptHours selectTraDeptHoursById(Long id);

    /**
     * 新增部门学时
     *
     * @param traDeptHours 部门学时信息
     * @return 状态
     */
    int insertTraDeptHours(TraDeptHours traDeptHours);

    /**
     * 修改部门学时
     *
     * @param traDeptHours 部门学时信息
     * @return 状态
     */
    int updateTraDeptHours(TraDeptHours traDeptHours);

    /**
     * 删除部门学时
     *
     * @param ids 部门学时id集合
     * @return 状态
     */
    int deleteTraDeptHoursByIds(Long[] ids);

    /**
     * 检查是否已经创建过部门学时目标
     *
     * @param deptId 部门id
     * @return 学时信息
     */
    TraDeptHours checkedDept(String deptId);
}
