package com.ruoyi.activiti.feign;

import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.feign.factory.RemoteFileFallback;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 文件 Feign服务层
 * 
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = "ruoyi-file", fallbackFactory = RemoteFileFallback.class)
public interface RemoteFileService
{

    @SneakyThrows(Exception.class)
    @PostMapping("/file/getUrls")
    public List<Map<String,Object>> getFileUrls(@RequestBody UrlVo urlVo);

    @PostMapping("/file/update")
    public int update(@RequestParam("parentId")Long parentId,@RequestParam(name = "tempId") String tempId);

    @PostMapping("/file/update_m")
    public int updateMongo(@RequestParam("parentId")String parentId,@RequestParam(name = "tempId") String tempId);

    /**
     * 获取文件列表
     * @param parentId
     * @param types
     * @return
     */
    @GetMapping("/file/list")
    public List<SysAttachment> getList(@RequestParam("parentId")Long parentId, @RequestParam("types")String types);
    /**
     * 获取文件列表
     * @param parentId
     * @param types
     * @return
     */
    @GetMapping("/file/list_m")
    public List<SysAttachment> getListMongo(@RequestParam("parentId")String parentId, @RequestParam("types")String types);
}
