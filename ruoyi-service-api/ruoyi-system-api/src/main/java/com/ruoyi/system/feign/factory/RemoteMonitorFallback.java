package com.ruoyi.system.feign.factory;

import com.ruoyi.system.domain.SysMonitor;
import com.ruoyi.system.feign.RemoteMonitorService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author yrb
 * @Date 2023/5/10 15:26
 * @Version 1.0
 * @Description
 */
@Slf4j
@Component
public class RemoteMonitorFallback implements FallbackFactory<RemoteMonitorService> {

    /**
     * @param throwable
     * @return
     */
    @Override
    public RemoteMonitorService create(Throwable throwable) {
        return new RemoteMonitorService() {

            /**
             * @return
             */
            @Override
            public SysMonitor getMonitorInfo() {
                return null;
            }
        };
    }
}
