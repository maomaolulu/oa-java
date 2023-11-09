package com.ruoyi.activiti.aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.BizBusinessMapper;
import com.ruoyi.activiti.service.MapInfoService;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.common.annotation.AuditFilter;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 审批切面
 *
 * @author zx
 * @date 2022/1/11 13:41
 */
@Aspect
@Slf4j
@Component
public class AuditAspect {
    private final String METHOD = "startProcess";
    private final String CC = "changeCC";
    private final RemoteUserService remoteUserService;
    private final MapInfoService mapInfoService;
    private final MailService mailService;
    private final BizBusinessMapper businessMapper;

    @Autowired
    public AuditAspect(RemoteUserService remoteUserService, MapInfoService mapInfoService, MailService mailService, BizBusinessMapper businessMapper) {
        this.remoteUserService = remoteUserService;
        this.mapInfoService = mapInfoService;
        this.mailService = mailService;
        this.businessMapper = businessMapper;
    }

    @Pointcut(value = "@annotation(com.ruoyi.common.annotation.AuditFilter)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void beforeAop(JoinPoint joinPoint) {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
//        System.out.println("方法请求路径" + request.getRequestURI());
//        System.out.println("请求方式" + request.getMethod());
//        System.out.println("请求参数" + JSON.toJSONString(joinPoint.getArgs()));
        System.out.println("进入切面");
        AuditFilter filter = null;
        try {
            filter = getAnnotationLog(joinPoint);
        } catch (Exception e) {
            log.error("获取注解信息", e);
        }
        if (filter != null && CC.equals(filter.method())) {
            BizBusiness business = new BizBusiness();
            try {
                business = com.ruoyi.common.json.JSON.unmarshal(JSON.toJSONString(joinPoint.getArgs()[0]), BizBusiness.class);
            } catch (Exception e) {
                log.error("获取请求参数", e);
            }
            String mapInfoMail = mapInfoService.getMapInfoMail(business);
            // 通知被抄送人
            List<Map<String, Object>> cc = businessMapper.getCC(business.getProcInstId());
            for (Map<String, Object> string : cc) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(string.get("cc_id").toString()));
                String txt2 = "<body>" +
                        "<p>" +
                        "您有一条" + business.getApplyer() + "抄送的信息，请及时查看。" +
                        "</p>" +
                        "<br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser.getEmail())) {
                    mailService.send(txt2, business.getTitle(), sysUser.getEmail(), sysUser.getUserName(),sysUser.getCid());
                }
            }
        }
    }

    @AfterReturning(returning = "value", pointcut = "pointCut()")
    public void autoAudit(JoinPoint joinPoint, Object value) {
        AuditFilter filter = null;
        try {
            filter = getAnnotationLog(joinPoint);
        } catch (Exception e) {
            log.error("获取注解信息", e);
        }
        if (filter != null && METHOD.equals(filter.method())) {
            Set<String> strings = new HashSet<>();
            if (value != null) {
                strings = (Set<String>) value;
            } else {
                log.error("发送邮件无待办人");
                return;
            }
            BizBusiness business = new BizBusiness();
            try {
                business = com.ruoyi.common.json.JSON.unmarshal(JSON.toJSONString(joinPoint.getArgs()[0]), BizBusiness.class);
            } catch (Exception e) {
                log.error("获取请求参数", e);
            }
            String mapInfoMail = mapInfoService.getMapInfoMail(business);
            // 通知待审批人
            SysUser user = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            for (String string : strings) {
                if (string.equals(String.valueOf(SystemUtil.getUserId()))) {
                    continue;
                }
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(string));
                String txt2 = "<body>" +
                        "<p>" +
                        "您有一条新的待办事项，请及时处理。" +
                        "</p>" +
                        "<br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser.getEmail())) {
                    mailService.send(txt2, business.getTitle(), sysUser.getEmail(), user.getUserName(),sysUser.getCid());
                }
            }
        }

    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private AuditFilter getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(AuditFilter.class);
        }
        return null;
    }
}
