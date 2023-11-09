package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.my_apply.BizSealApply;
import com.ruoyi.activiti.domain.vo.SealApplyVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用印申请
 * @author zx
 * @date 2022/1/12 17:32
 */
@Repository
public interface BizSealApplyMapper extends BaseMapper<BizSealApply> {
    @Select("SELECT bu.id,bu.proc_inst_id,bu.title,bu.current_task,bu.auditors,bu.result,bu.`status`,bu.del_flag, " +
            "sa.apply_code,d.dept_name as user_dept,u.user_name as seal_user,sa.stamp_date, " +
            "sa.document,sa.document_num,sa.create_time,sa.create_by,sa.seal_type,sa.document_type " +
            "FROM  biz_business bu " +
            "INNER JOIN biz_seal_apply sa on bu.table_id = sa.id " +
            "LEFT JOIN sys_dept d on d.dept_id = sa.user_dept " +
            "LEFT JOIN sys_user u on u.user_id = sa.seal_user ${ew.customSqlSegment} " +
            "order by sa.id desc ")
    List<SealApplyVo> selectVo(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
