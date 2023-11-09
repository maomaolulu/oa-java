package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.proc.BizLeave;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 请假Mapper接口
 * 
 * @author ruoyi
 * @date 2020-01-07
 */
@Repository
public interface BizLeaveMapper
{
    /**
     * 查询请假
     * 
     * @param id 请假ID
     * @return 请假
     */
    public BizLeave selectBizLeaveById(String id);

    /**
     * 查询请假列表
     * 
     * @param actLeave 请假
     * @return 请假集合
     */
    public List<BizLeave> selectBizLeaveList(BizLeave actLeave);

    /**
     * 新增请假
     * 
     * @param actLeave 请假
     * @return 结果
     */
    public int insertBizLeave(BizLeave actLeave);

    /**
     * 修改请假
     * 
     * @param actLeave 请假
     * @return 结果
     */
    public int updateBizLeave(BizLeave actLeave);

    /**
     * 删除请假
     * 
     * @param id 请假ID
     * @return 结果
     */
    public int deleteBizLeaveById(String id);

    /**
     * 批量删除请假
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizLeaveByIds(String[] ids);

    @Select("select d.* from sys_dept d  ${ew.customSqlSegment} ")
    public List<Object> getSql(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
