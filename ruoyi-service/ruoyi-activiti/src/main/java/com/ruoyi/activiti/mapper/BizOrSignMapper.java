package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.my_apply.BizOrSign;

import com.ruoyi.activiti.domain.proc.BizBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/6 16:58
 */
@Repository
public interface BizOrSignMapper  extends BaseMapper<BizOrSign> {
    /**
     * 获得我的或签流程
     */
    @Select("SELECT n.* FROM biz_or_sign b LEFT JOIN biz_business n on b.process_key = n.proc_inst_id ${ew.customSqlSegment }")
    List<BizBusiness> getOrSign(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    @Select("SELECT * FROM biz_or_sign where process_key = #{process_key} and user_id = #{user_id}")
    BizBusiness isExist(@Param("process_key") String processKey,@Param("user_id") Long userId );
}
