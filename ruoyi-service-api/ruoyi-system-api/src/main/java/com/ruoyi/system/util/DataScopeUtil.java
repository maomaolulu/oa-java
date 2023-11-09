package com.ruoyi.system.util;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author zx
 * @date 2021/12/9 15:06
 */
@Slf4j
@Component
public class DataScopeUtil {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";
    /**
     * 公司数据权限
     */
    public static final String DATA_COMPANY_DEPT = "6";
    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";


    /**
     * 获取权限sql
     *
     * @param deptAlias 部门表别名
     * @param userAlias 用户表别名
     * @return
     */
    public String getScopeSql(SysUser user, String deptAlias, String userAlias) {

        if (user != null) {
            // 如果是超级管理员，则不过滤数据
            if (user.isAdmin()) {
                return "";
            }
        } else {
            log.warn("数据权限拦截失败,执行对象 user is null");
        }
        StringBuilder sqlString = new StringBuilder();
        for (SysRole role : user.getRoles()) {
            String dataScope = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope) && StringUtils.isNotBlank(deptAlias)) {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                        role.getRoleId()));
            } else if (DATA_SCOPE_DEPT.equals(dataScope) && StringUtils.isNotBlank(deptAlias)) {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                if ("0".equals(user.getDeptId())) {
                    sqlString = new StringBuilder();
                    break;
                }
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or ancestors LIKE '%,{},%' )",
                        deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (DATA_COMPANY_DEPT.equals(dataScope)) {
                if ("0".equals(user.getDeptId())) {
                    sqlString = new StringBuilder();
                    break;
                }
                Long companyId = SystemUtil.getCompanyId();
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or ancestors LIKE '%,{},%' )",
                        deptAlias, companyId, companyId));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StringUtils.isNotBlank(userAlias)) {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                } else {
                    sqlString.append(StringUtils.format(" OR {}.dept_id IS NULL ", deptAlias));
                }
            }
        }
        String format = StringUtils.isBlank(deptAlias) ? "" : StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_user_dept WHERE user_id = {} ) ", deptAlias, user.getUserId());
        if (StringUtils.isNotBlank(sqlString.toString())) {
            return "(" + sqlString.substring(4) + format + ")";
        }
        return "";
    }
}
