package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.proc.BizBusinessPlus;
import com.ruoyi.activiti.domain.dto.NeedManageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zx
 * @date 2022/1/19 16:11
 */
@Repository
public interface BizBusinessPlusMapper extends BaseMapper<BizBusinessPlus> {
    @Select("SELECT count(0) FROM( " +
            "SELECT  bu.id, bu.proc_def_id, bu.proc_def_key, bu.proc_inst_id, bu.proc_name, bu.current_task, bu.result, bu.status, bu.table_id, bu.title, bu.user_id, bu.applyer, bu.apply_time, bu.del_flag, bu.auditors, bu.last_auditor, bu.last_auditor_time, " +
            "d.ancestors,d.dept_id, SUM(ht.DURATION_) as duration " +
            "FROM biz_business bu " +
            "LEFT JOIN sys_user u on u.user_id = bu.user_id  " +
            "LEFT JOIN sys_dept d on d.dept_id = u.dept_id " +
            "LEFT JOIN ACT_HI_TASKINST ht on ht.PROC_INST_ID_ = bu.proc_inst_id " +
            "where ${sql} " +
            "GROUP BY bu.id   " +
            ") as a " +
            "${ew.customSqlSegment} ")
    int listCount(@Param(Constants.WRAPPER) QueryWrapper wrapper,@Param("sql") String sql);

    @Select("SELECT * FROM( " +
            "SELECT  bu.id, bu.proc_def_id, bu.proc_def_key, bu.proc_inst_id, bu.proc_name, bu.current_task, bu.result, bu.status, bu.table_id, bu.title, bu.user_id, bu.applyer, bu.apply_time, bu.del_flag, bu.auditors, bu.last_auditor, bu.last_auditor_time, " +
            "d.ancestors,d.dept_id,case when SUM(ht.DURATION_)  is null then 0 else SUM(ht.DURATION_) end as duration " +
            "FROM biz_business bu " +
            "LEFT JOIN sys_user u on u.user_id = bu.user_id  " +
            "LEFT JOIN sys_dept d on d.dept_id = u.dept_id " +
            "LEFT JOIN ACT_HI_TASKINST ht on ht.PROC_INST_ID_ = bu.proc_inst_id " +
            "where ${sql} " +
            "GROUP BY bu.id   " +
            ") as a " +
            "${ew.customSqlSegment} ")
    List<Map<String,Object>> listProc(@Param(Constants.WRAPPER) QueryWrapper wrapper,@Param("sql") String sql);

    @Select("SELECT a.dept_id,a.dept_name FROM sys_dept a WHERE a.is_company = 1 and a.dept_id  not in (268,269) ")
    List<Map<String,Object>> listCompany();

    @Select("SELECT  " +
            "b.id, b.proc_def_id, b.proc_def_key, b.proc_inst_id, b.proc_name, b.current_task, b.result, b.status, b.table_id, b.title, b.user_id, b.applyer, b.apply_time, b.del_flag, b.auditors, b.last_auditor, b.last_auditor_time, " +
            "f.implementation_reasons, " +
            "f.content, " +
            "f.expected_effect, " +
            "f.apply_code, " +
            "d.dept_name, " +
            "c.dept_name as company_name," +
            "f.affiliated_system " +
            "FROM   " +
            "biz_business b " +
            "LEFT JOIN biz_demand_feedback f on f.id = b.table_id " +
            "LEFT JOIN sys_dept d on d.dept_id = f.dept_id " +
            "LEFT JOIN sys_dept c on c.dept_id = f.company_id " +
            "${ew.customSqlSegment} ")
    List<NeedManageDto> getNeed(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
