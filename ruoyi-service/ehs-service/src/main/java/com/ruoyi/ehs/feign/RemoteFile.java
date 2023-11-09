package com.ruoyi.ehs.feign;

import com.ruoyi.system.domain.SysDictData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/19 17:16
 */
@FeignClient(name = "ruoyi-file")
public interface RemoteFile {
    /**
     * 临时文件转有效文件
     * @param tempId
     * @param types
     * @return
     */
    @PostMapping("/file/update")
    public int update(@RequestParam("parentId")Long parentId,@RequestParam(name = "tempId") String tempId, @RequestParam(name = "types") String types);
}

