package com.ruoyi.gateway.fiflt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.gateway.config.properties.IgnoreWhiteProperties;
import com.ruoyi.gateway.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网关鉴权
 */
@Slf4j
@Component
@RefreshScope
public class AuthFilter implements GlobalFilter, Ordered
{
    /**
     * 1小时后过期
     */
    private final static long   EXPIRE        = 6 * 60 * 60;
    /** 排除过滤的 uri 地址
     *
     * swagger排除自行添加
     */
    private static final String[] whiteList = {"/auth/login", "/auth/login/wx"};
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;
    @Autowired
    public RedisService redisService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        String url = exchange.getRequest().getURI().getPath();
        // 跳过不需要验证的路径
        // 固定白名单
        List<String> strings = Arrays.asList(whiteList);
        //TODO nacos动态配置白名单
        List<String> whites = ignoreWhite.getWhites();
        for (String white : whites) {
            log.debug(white);
            if(white.contains("stop_")&&url.contains("maintaince")){
              return setMaintainceResponse(exchange,white.replace("stop_",""),402);
            }
        }
        if(whites.contains("stop")&&!whites.contains(url)){
            return setUnauthorizedResponse(exchange,"系统维护中",401);
        }
        if(url.indexOf("maintaince")!=-1){
            return setMaintainceResponse(exchange,"",200);
        }
        if(url.indexOf("minio/")!=-1){
            return chain.filter(exchange);
        }
        if(url.contains("service/model")){
            return chain.filter(exchange);
        }
        if(url.contains("favicon.ico")){
            return chain.filter(exchange);
        }
        if(url.contains("editor-app/")){
            return chain.filter(exchange);
        }
        if (strings.contains(url)||whites.contains(url))
        {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN);
        // token为空
        if (StringUtils.isBlank(token))
        {
            log.error("token为空");
            return setUnauthorizedResponse(exchange, "token can't null or empty string",401);
        }
        log.info("前端传的token:{}", token);
        // 若pc端无token则判断移动端token
        String userStr = redisService.getCacheObject(Constants.ACCESS_TOKEN + token + "_pc");
        String type = "_pc";
        if(userStr==null){
            userStr = redisService.getCacheObject(Constants.ACCESS_TOKEN + token + "_wx");
            type = "_wx";
            if(userStr==null){
                userStr = redisService.getCacheObject(Constants.ACCESS_TOKEN + token + "_app");
                type = "_app";
            }
        }

        if (StringUtils.isBlank(userStr))
        {
            log.error("token匹配失败");
            return setUnauthorizedResponse(exchange, "token verify error",401);
        }
//        log.info("缓存中的token:{}",userStr);

        JSONObject jo = JSONObject.parseObject(userStr);
        log.error(jo.toJSONString());
        String userId = jo.getString("userId");
        log.info(userId+type+"操作:{}", url);
        // 查询token信息
        if (StringUtils.isBlank(userId))
        {
            return setUnauthorizedResponse(exchange, "token verify error",401);
        }
        // 刷新token
        redisService.setCacheObject(Constants.ACCESS_TOKEN + token + type,userStr,EXPIRE, TimeUnit.SECONDS);
        redisService.setCacheObject(Constants.ACCESS_USERID + userId + type,token,EXPIRE, TimeUnit.SECONDS);

        // 设置userId到request里，后续根据userId，获取用户信息
        String encode = "";
        String company = "";
        try {
            encode = URLEncoder.encode(jo.getString("userName"), "UTF-8");
            company = URLEncoder.encode(jo.getString("company"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header(Constants.CURRENT_ID, userId)
                .header(Constants.CURRENT_USERNAME, jo.getString("loginName"))
                .header(Constants.CN_USERNAME,encode)
                .header(Constants.COMPANY_ID,jo.getString( "companyId"))
                .header(Constants.COMPANY_NAME,company)
                .header(Constants.DEPARTMENT_ID,jo.getString( "deptId"))
                .build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String msg,Integer code)
    {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try
        {
            response = JSON.toJSONString(R.error(code, msg)).getBytes(Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    private Mono<Void> setMaintainceResponse(ServerWebExchange exchange, String msg,Integer code)
    {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.OK);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try
        {
            response = JSON.toJSONString(R.error(code, msg)).getBytes(Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder()
    {
        return -200;
    }
}