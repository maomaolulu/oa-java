package com.ruoyi.common.exception;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.exception.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 * @author zmr
 * @author lucas
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    public R handleException(HttpRequestMethodNotSupportedException e)
    {
        logger.error(e.getMessage(), e);
        return R.error("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R notFount(RuntimeException e)
    {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
        {
            throw e;
        }
        if(e instanceof UserNotExistsException){
            logger.error("运行时异常:", e);
            return R.error("用户不存在");
        }
        if(e instanceof UserDeleteException){
            logger.error("运行时异常:", e);
            return R.error("用户已注销");
        }
        if(e instanceof UserBlockedException){
            logger.error("运行时异常:", e);
            return R.error("用户已停用，请联系系统管理员");
        }
        if(e instanceof UserPasswordNotMatchException){
            logger.error("运行时异常:", e);
            return R.error("密码错误");
        }
        if(e instanceof UserNameNotSpecificationException){
            logger.error("运行时异常:", e);
            return R.error("用户名错误");
        }
        if(e instanceof UserPasswordNotSpecificationException){
            logger.error("运行时异常:", e);
            return R.error("密码错误");
        }
        if(e instanceof BadSqlGrammarException){
            logger.error("运行时异常:", e);
            return R.error("系统繁忙，请稍后再试！（002）");
        }
        if(e instanceof NullPointerException){
            logger.error("运行时异常:", e);
            return R.error("系统繁忙，请稍后再试！（003）");
        }
        if(e.getMessage().contains("Cannot find task with id")){
            logger.error("运行时异常:", e);
            return R.error("当前页面已过期，请刷新列表");
        }
        logger.error("运行时异常:", e);
        return R.error("系统繁忙，请稍后再试！（001）");
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RuoyiException.class)
    public R handleWindException(RuoyiException e)
    {
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e)
    {
        logger.error(e.getMessage(), e);
        return R.error("数据库中已存在该记录");
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) throws Exception
    {
        logger.error(e.getMessage(), e);
        return R.error("服务器错误，请联系管理员");
    }

    /**
     * 捕获并处理未授权异常
     *
     * @param e 授权异常
     * @return 统一封装的结果类, 含有代码code和提示信息msg
     */
    @ExceptionHandler(UnauthorizedException.class)
    public R handle401(UnauthorizedException e)
    {
        return R.error(401, e.getMessage());
    }

    // 验证码错误
    @ExceptionHandler(ValidateCodeException.class)
    public R handleCaptcha(ValidateCodeException e)
    {
        return R.error(e.getMessage());
    }
}