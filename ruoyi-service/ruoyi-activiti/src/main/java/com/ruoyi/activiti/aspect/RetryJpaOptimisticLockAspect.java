package com.ruoyi.activiti.aspect;

import com.ruoyi.activiti.annotation.RetryJpaOptimisticLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * jpa乐观锁异常ObjectOptimisticLockingFailureException重试机制 通过AOP统一拦截这种异常并进行一定次数的重试
 *
 * @author zx
 * @className RetryOnOptimisticLockingFailure
 * @date 2022-10-20 17:20:38
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class RetryJpaOptimisticLockAspect {

    private int maxRetries;

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Pointcut("@annotation(com.ruoyi.activiti.annotation.RetryJpaOptimisticLock)")
    public void retryOnFailure(){}

    @Around("retryOnFailure()")
    public Object retry(ProceedingJoinPoint pjp) throws Throwable {
        // 获取拦截的方法名
        MethodSignature msig = (MethodSignature) pjp.getSignature();
        // 返回被织入增加处理目标对象
        Object target = pjp.getTarget();
        // 为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        // 获取注解信息
        RetryJpaOptimisticLock annotation = currentMethod.getAnnotation(RetryJpaOptimisticLock.class);
        // 设置重试次数
        this.setMaxRetries(annotation.times());
        // 重试次数
        int count = 0;
        OptimisticLockingFailureException lockFailureException;
        do {
            count++;
            try {
                // 再次执行业务代码
                return pjp.proceed();
            } catch (OptimisticLockingFailureException ex) {
                log.error("jpa乐观锁异常ObjectOptimisticLockingFailureException重试失败, count:{}",count);
                lockFailureException = ex;
            }
        } while (count < maxRetries);
        log.error("============ jpa乐观锁异常ObjectOptimisticLockingFailureException".concat(String.valueOf(maxRetries)).concat("次机会全部重试失败 ==========="));
        throw lockFailureException;
    }

}
