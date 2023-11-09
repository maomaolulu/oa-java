package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.proc.ActReProcdefPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * @date 2022/3/31 16:31
 */
@Repository
public interface ActReProcdefPlusMapper extends BaseMapper<ActReProcdefPlus> {
    @Select("SELECT ACT_RE_PROCDEF.*,b.DEPLOY_TIME_ FROM `ACT_RE_PROCDEF`  " +
            "LEFT JOIN ACT_RE_DEPLOYMENT b on DEPLOYMENT_ID_ = b.ID_ ${ew.customSqlSegment} ")
    List<ActReProcdefPlus> getList(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
