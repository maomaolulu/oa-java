package com.ruoyi.gateway.fiflt;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.gateway.config.properties.CaptchaProperties;
import com.ruoyi.gateway.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 验证码过滤器
 * 
 * @author ruoyi
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object>
{
    private final static String[] VALIDATE_URL = new String[] { "/login" };

    @Autowired
    private ValidateCodeService validateCodeService;

    @Autowired
    private CaptchaProperties captchaProperties;

    private static final String CODE = "code";

    private static final String UUID = "uuid";

    @Override
    public GatewayFilter apply(Object config)
    {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            Boolean enabled = captchaProperties.getEnabled();
            // 非登录或验证码关闭，不处理
            if (!Arrays.asList(VALIDATE_URL).contains(path) || !enabled)
            {
                return chain.filter(exchange);
            }

            try
            {
                String rspStr = resolveBodyFromRequest(request);
                JSONObject obj = JSONObject.parseObject(rspStr);
                validateCodeService.checkCapcha(obj.getString(CODE), obj.getString(UUID));
                System.out.println("验证码校验通过");
            }
            catch (Exception e)
            {
                return ServletUtils.webFluxResponseWriter(exchange.getResponse(), e.getMessage());
            }
            return chain.filter(exchange);
        };
    }

    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest)
    {
        // 获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }
}
