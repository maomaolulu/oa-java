package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.proc.BizNode;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * <p>File：BizNodeMapper.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月14日 上午11:11:05</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
@Repository
public interface BizNodeMapper extends BaseMapper<BizNode>
{
    /**
     * 查询这个角色有这个部门权限的人
     * @param deptId
     * @param roleId
     * @return
     */
    @Select("select distinct a.user_id from sys_user_auth a where a.dept_id = #{deptId} and a.role_id = #{roleId}")
    Set<Long> getAuditors(@Param("deptId")Long deptId, @Param("roleId")Long roleId);
}
