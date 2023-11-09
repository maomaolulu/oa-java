package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.BizProcesskeyRole;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/4/17 15:45
 * @Version 1.0
 * @Description
 */
@Repository
public interface BizProcesskeyRoleMapper extends BaseMapper<BizProcesskeyRole> {
    /**
     * 通过processkey获取角色id列表
     * @param processkey 参数
     * @return 集合
     */
    @Select("select distinct role_id from biz_processkey_role where process_key = #{processkey} ")
    List<Integer> selectRoleByBizProcesskey(@Param("processkey") String processkey);

    /**
     * 通过processkey删除存在的对应关系
     * @param processkey 参数
     * @return result
     */
    @Delete("delete from biz_processkey_role where process_key = #{processkey} ")
    int deleteByProcessKey(@Param("processkey") String processkey);
}
