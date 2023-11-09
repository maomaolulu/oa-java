package com.ruoyi.system.feign;

import com.ruoyi.common.constant.ServiceNameConstants;
import com.ruoyi.system.feign.factory.RemoteDictFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户 Feign服务层
 * 
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictFallback.class)
public interface RemoteDictService
{

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @GetMapping("dict/data/label")
    public String getLabel(@RequestParam("dictType") String dictType,@RequestParam("dictValue") String dictValue);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictCode  字典编码
     * @return 字典标签
     */
    @GetMapping("dict/data/label_code")
    public String getLabelByCode(@RequestParam("dictCode") String dictCode);


}
