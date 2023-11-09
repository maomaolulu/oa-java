package com.ruoyi.daily.aspect;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.annotation.MailRecordFilter;
import com.ruoyi.daily.service.government_centre.BizMailRecordService;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 邮件记录切面
 *
 * @author zx
 * @date 2022/1/11 13:41
 */
@Aspect
@Slf4j
@Component
public class MailAspect {
    private final String METHOD = "sensitive";
    private final BizMailRecordService mailRecordService;
    @Autowired
    public MailAspect(BizMailRecordService mailRecordService) {
        this.mailRecordService = mailRecordService;
    }

    @Pointcut(value = "@annotation(com.ruoyi.common.annotation.MailRecordFilter)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void beforeAop(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        System.out.println("方法请求路径" + request.getRequestURI());
        System.out.println("请求方式" + request.getMethod());
        System.out.println("请求参数" + JSON.toJSONString(joinPoint.getArgs()));
//        AuditFilter filter = null;
//        try {
//            filter = getAnnotationLog(joinPoint);
//        } catch (Exception e) {
//            log.error("获取注解信息",e);
//        }
//        if (filter == null||!METHOD.equals(filter.method()))
//        {
//            return;
//        }
//        BizMailRecord bizMailRecord = new BizMailRecord();
//        try {
//            bizMailRecord = com.ruoyi.common.json.JSON.unmarshal(JSON.toJSONString(joinPoint.getArgs()[0]), BizMailRecord.class);
//        } catch (Exception e) {
//            log.error("获取请求参数",e);
//        }
    }

    @AfterReturning(returning = "value", pointcut = "pointCut()")
    public void autoAudit(JoinPoint joinPoint, Object value) {

    }
    /**
     * 是否存在注解，如果存在就获取
     */
    private MailRecordFilter getAnnotationLog(JoinPoint joinPoint) throws Exception
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null)
        {
            return method.getAnnotation(MailRecordFilter.class);
        }
        return null;
    }
}
