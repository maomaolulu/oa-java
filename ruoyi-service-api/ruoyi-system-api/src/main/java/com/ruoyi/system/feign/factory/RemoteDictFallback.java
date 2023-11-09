package com.ruoyi.system.feign.factory;

import com.ruoyi.system.feign.RemoteDictService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDictFallback implements FallbackFactory<RemoteDictService>
{/* (non-Javadoc)
  * @see feign.hystrix.FallbackFactory#create(java.lang.Throwable)
  */
    @Override
    public RemoteDictService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteDictService()
        {


            /**
             * 根据字典类型和字典键值查询字典数据信息
             *
             * @param dictType  字典类型
             * @param dictValue 字典键值
             * @return 字典标签
             */
            @Override
            public String getLabel(String dictType, String dictValue) {
                return "";
            }

            @Override
            public String getLabelByCode(String dictCode) {
                return "";
            }
        };
    }
}
