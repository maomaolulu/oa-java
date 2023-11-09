package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.proc.ActRuTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ActRuTaskMapper
{
    int deleteByPrimaryKey(String id);

    int insert(ActRuTask record);

    int insertSelective(ActRuTask record);

    ActRuTask selectByPrimaryKey(String id);
    @Select("SELECT " +
            " ID_, " +
            " REV_, " +
            " EXECUTION_ID_, " +
            " PROC_INST_ID_, " +
            " PROC_DEF_ID_, " +
            " NAME_, " +
            " PARENT_TASK_ID_, " +
            " DESCRIPTION_, " +
            " TASK_DEF_KEY_, " +
            " OWNER_, " +
            " ASSIGNEE_, " +
            " DELEGATION_, " +
            " PRIORITY_, " +
            " CREATE_TIME_, " +
            " DUE_DATE_, " +
            " CATEGORY_, " +
            " SUSPENSION_STATE_, " +
            " TENANT_ID_, " +
            " FORM_KEY_  " +
            "FROM " +
            " ACT_RU_TASK where ID_ = #{id}")
    ActRuTask selectByPrimaryKey1(@Param("id") String id);

    @Select("SELECT " +
            " ID_, " +
            " REV_, " +
            " EXECUTION_ID_, " +
            " PROC_INST_ID_, " +
            " PROC_DEF_ID_, " +
            " NAME_, " +
            " PARENT_TASK_ID_, " +
            " DESCRIPTION_, " +
            " TASK_DEF_KEY_, " +
            " OWNER_, " +
            " ASSIGNEE_, " +
            " DELEGATION_, " +
            " PRIORITY_, " +
            " CREATE_TIME_, " +
            " DUE_DATE_, " +
            " CATEGORY_, " +
            " SUSPENSION_STATE_, " +
            " TENANT_ID_, " +
            " FORM_KEY_  " +
            "FROM " +
            " ACT_RU_TASK where PROC_INST_ID_ = #{procInstId}")
    ActRuTask selectByProcInstId(@Param("procInstId") String procInstId);

    @Select("SELECT " +
            " ID_, " +
            " REV_, " +
            " EXECUTION_ID_, " +
            " PROC_INST_ID_, " +
            " PROC_DEF_ID_, " +
            " NAME_, " +
            " PARENT_TASK_ID_, " +
            " DESCRIPTION_, " +
            " TASK_DEF_KEY_, " +
            " OWNER_, " +
            " ASSIGNEE_, " +
            " DELEGATION_, " +
            " PRIORITY_, " +
            " CREATE_TIME_, " +
            " DUE_DATE_, " +
            " CATEGORY_, " +
            " SUSPENSION_STATE_, " +
            " TENANT_ID_, " +
            " FORM_KEY_  " +
            "FROM " +
            " ACT_RU_TASK where PROC_INST_ID_ = #{procInstId}")
    List<ActRuTask> selectListByProcInstId(@Param("procInstId") String procInstId);


    int updateByPrimaryKeySelective(ActRuTask record);

    int updateByPrimaryKey(ActRuTask record);

    List<ActRuTask> selectAll();
}
