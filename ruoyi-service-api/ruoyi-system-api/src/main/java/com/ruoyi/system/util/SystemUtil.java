package com.ruoyi.system.util;

import cn.hutool.core.exceptions.StatefulException;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @description: 系统信息工具类
 * @author: zx
 * @date: 2021/11/12 10:59
 */
public class SystemUtil {
    private SystemUtil() {
    }

    public static Long getUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        String currentId = request.getHeader(Constants.CURRENT_ID);
        if (StringUtils.isNotBlank(currentId)) {
            return Long.valueOf(currentId);
        }
        return null;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static long getCurrentUserId() {
        String currentId = getRequest().getHeader(Constants.CURRENT_ID);
        if (StringUtils.isNotBlank(currentId)) {
            return Long.valueOf(currentId);
        }
        return 0L;
    }

    public static String getUserName() {
        return getRequest().getHeader(Constants.CURRENT_USERNAME);
    }

    public static String getUserNameCn() {
        try {
            return URLDecoder.decode(getRequest().getHeader(Constants.CN_USERNAME), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
           throw new StatefulException("获取用户中文名称失败");
        }
    }
    /**
     * 获取公司id
     */
    public static Long getCompanyId() {
        String companyId = getRequest().getHeader(Constants.COMPANY_ID);
        if (StringUtils.isNotBlank(companyId)) {
            return Long.valueOf(companyId);
        }
        return null;
    }

    /**
     * 获取公司名称
     */
    public static String getCompanyName() {

        try {
            return URLDecoder.decode(getRequest().getHeader(Constants.COMPANY_NAME), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new StatefulException("获取公司名称失败");
        }
    }

    /**
     * 获取部门id
     */
    public static Long getDeptId() {
        String deptId = getRequest().getHeader(Constants.DEPARTMENT_ID);
        if (StringUtils.isNotBlank(deptId)) {
            return Long.valueOf(deptId);
        }
        return null;
    }

}
