package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.ActComment;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * @date 2022/2/21 15:43
 */
@Repository
public interface ActCommentMapper extends BaseMapper<ActComment> {
    @Select("SELECT ac.id,case when ac.del_flag = '1' then '该评论已撤回' else ac.comment end as comment ,u.user_name,ac.create_time,ac.del_flag,ac.user_id  " +
            "FROM " +
            "biz_act_comment ac " +
            "LEFT JOIN sys_user u on u.user_id = ac.user_id " +
            "${ew.customSqlSegment} ")
    List<ActComment> selectComment(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    @Select("SELECT * from biz_business  ${ew.customSqlSegment} ")
    BizBusiness selectBusiness(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
