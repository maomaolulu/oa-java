package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>File：ActReProcdefMapper.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 上午10:20:27</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
@Repository
public interface ActReProcdefMapper extends BaseMapper<ActReProcdef>
{
    /**
     * 获取关联审批单下拉列表
     * @return
     */
    @Select("SELECT m.NAME_ as name,m.KEY_ as id  " +
            " FROM `ACT_RE_PROCDEF` m " +
            " WHERE m.VERSION_ = (SELECT MAX(VERSION_) FROM ACT_RE_PROCDEF  WHERE KEY_ = m.KEY_) ")
   List<Map<String,Object>> getDefinedList();

    /**
     * 获取流程名称
     * @param wrapper
     * @return
     */
    @Select("SELECT m.NAME_ as name,p.KEY_ as id  FROM `ACT_RE_MODEL` m " +
            "INNER JOIN ACT_RE_PROCDEF p ON  m.DEPLOYMENT_ID_ = p.DEPLOYMENT_ID_ " +
            "${ew.customSqlSegment}")
    Map<String,Object> getDefinedName(@Param(Constants.WRAPPER) Wrapper wrapper);
}
