//package com.ruoyi.system.utils;
//
//import com.ruoyi.common.constant.Constants;
//import com.ruoyi.common.utils.ServletUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// * @description: 系统信息工具类
// * @author: zx
// * @date: 2021/11/12 10:59
// */
//public class SystemUtil {
//    private SystemUtil() {
//    }
//
//    public static Long getUserId(){
//        HttpServletRequest request = ServletUtils.getRequest();
//        String currentId = request.getHeader(Constants.CURRENT_ID);
//        if (StringUtils.isNotBlank(currentId))
//        {
//            return Long.valueOf(currentId);
//        }
//        return null;
//    }
//    /**
//     * 获取request
//     */
//    public static HttpServletRequest getRequest()
//    {
//        return ServletUtils.getRequest();
//    }
//
//    /**
//     * 获取response
//     */
//    public static HttpServletResponse getResponse()
//    {
//        return ServletUtils.getResponse();
//    }
//
//    /**
//     * 获取session
//     */
//    public static HttpSession getSession()
//    {
//        return getRequest().getSession();
//    }
//
//    public static long getCurrentUserId()
//    {
//        String currentId = getRequest().getHeader(Constants.CURRENT_ID);
//        if (StringUtils.isNotBlank(currentId))
//        {
//            return Long.valueOf(currentId);
//        }
//        return 0L;
//    }
//
//    public static String getUserName()
//    {
//        return getRequest().getHeader(Constants.CURRENT_USERNAME);
//    }
//}
