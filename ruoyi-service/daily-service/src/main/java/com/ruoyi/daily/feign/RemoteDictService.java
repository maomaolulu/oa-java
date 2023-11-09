package com.ruoyi.daily.feign;

import com.ruoyi.system.domain.SysDictData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 系统 Feign服务层
 *
 * @author zx
 * @date 2022-08-12 11:17:42
 */
@FeignClient(name = "ruoyi-system")
public interface RemoteDictService {

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 参数键值
     */
    @GetMapping("dict/data/type")
    List<SysDictData> getType(@RequestParam("dictType")String dictType);

}
