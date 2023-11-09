package com.ruoyi.daily.mapper.daily;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.BizDaily;
import com.ruoyi.daily.domain.dto.DailyVisitDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日报
 *
 * @author zx
 * @date 2022/1/8 16:27
 */
@Repository
public interface DailyMapper extends BaseMapper<BizDaily> {
    @Select("SELECT COUNT(*)as num FROM sys_user_role WHERE role_id = #{roleId} AND user_id = #{userId}")
    Integer getRoleNum(@Param("roleId") Integer roleId, @Param("userId") Long userId);

    @Select("select t2.user_id,t2.clock_in_date,t2.create_by_name,t2.company,t1.factory,t1.address " +
            "from daily_visit_record_info t1 left join daily_visit_record t2 on t1.visit_record_id = t2.id " +
            "${ew.customSqlSegment} ")
    List<DailyVisitDto> getList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
