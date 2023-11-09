package com.ruoyi.daily.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.daily.domain.sys.SysUserAuth;
import com.ruoyi.system.domain.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * 权限管理
 * @author zx
 * @date 2022-10-21 17:39:27
 */
@Repository
public interface SysUserAuthMapper extends BaseMapper<SysUserAuth> {
    /**
     * 查询用户角色
     * @param userId
     * @return
     */
    @Select("select a.role_id,a.role_name,a.remark from sys_role a " +
            "left join sys_user_role b on b.role_id = a.role_id " +
            "where b.user_id = #{userId}")
    List<SysRole> getRoleByUserId(@Param("userId")Long userId);
}
