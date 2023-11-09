package com.ruoyi.system.feign.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RemoteConfigFallback implements FallbackFactory<RemoteConfigService>
{/* (non-Javadoc)
  * @see feign.hystrix.FallbackFactory#create(java.lang.Throwable)
  */
    @Override
    public RemoteConfigService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteConfigService()
        {


            /**
             * 查询参数配置列表
             *
             * @param sysConfig
             */
            @Override
            public List<SysConfig> listOperating(SysConfig sysConfig) {
                return new ArrayList<>();
            }

            /**
             * @return sysConfig
             */
            @Override
            public SysConfig findConfigUrl() {
                return null;
            }
        };
    }
}
