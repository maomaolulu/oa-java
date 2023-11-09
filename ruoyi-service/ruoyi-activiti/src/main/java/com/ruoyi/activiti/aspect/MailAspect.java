package com.ruoyi.activiti.aspect;

import com.alibaba.fastjson.JSON;
import com.ruoyi.activiti.domain.government_centre.BizMailRecord;
import com.ruoyi.activiti.feign.RemoteMailRecordService;
import com.ruoyi.common.annotation.MailRecordFilter;
//import com.ruoyi.daily.service.government_centre.BizMailRecordService;
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
    private final RemoteMailRecordService mailRecordService;

    @Autowired
    public MailAspect(RemoteMailRecordService mailRecordService) {
        this.mailRecordService = mailRecordService;
    }


    @Pointcut(value = "@annotation(com.ruoyi.common.annotation.MailRecordFilter)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void beforeAop(JoinPoint joinPoint) {
        System.out.println("请求参数" + JSON.toJSONString(joinPoint.getArgs()));
//        String text = joinPoint.getArgs()[0].toString();
//        String subject = joinPoint.getArgs()[1].toString();
//        String email = joinPoint.getArgs()[2].toString();
//        String createBy = joinPoint.getArgs()[3].toString();
//        BizMailRecord bizMailRecord = new BizMailRecord();
//        bizMailRecord.setContent(text);
//        bizMailRecord.setCreateBy(createBy);
//        bizMailRecord.setSubject(subject);
//        bizMailRecord.setSendTo(email);
//        mailRecordService.save(bizMailRecord);

    }

    @AfterReturning(returning = "value", pointcut = "pointCut()")
    public void autoAudit(JoinPoint joinPoint, Object value) {
        BizMailRecord bizMailRecord = new BizMailRecord();

        System.out.println("请求参数" + JSON.toJSONString(joinPoint.getArgs()));
        String text = joinPoint.getArgs()[0].toString();
        String subject = joinPoint.getArgs()[1].toString();
        String email = joinPoint.getArgs()[2].toString();
        String createBy = joinPoint.getArgs()[3].toString();
        bizMailRecord.setContent(text);
        bizMailRecord.setCreateBy(createBy);
        bizMailRecord.setSubject(subject);
        bizMailRecord.setSendTo(email);
        if(Boolean.valueOf(value.toString())){
            log.info(email+"发送成功");
            bizMailRecord.setSendStatus("已发送");
        }else {
            log.error(email+"发送失败");
            bizMailRecord.setSendStatus("发送失败");
        }
        mailRecordService.save(bizMailRecord);

    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private MailRecordFilter getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(MailRecordFilter.class);
        }
        return null;
    }
}
