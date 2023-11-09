package com.ruoyi.system.feign;

import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.feign.factory.RemoteFileFallback;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    /**
     * 获取url永久
     *
     * @param bucketName
     * @return
     */
    @GetMapping("/file/getUrls")
    public String getFileUrls(@RequestParam("bucketName")String bucketName,@RequestParam("path") String path);

    @PostMapping("/file/update")
    public int update(@RequestParam("parentId")Long parentId,@RequestParam(name = "tempId") String tempId);

    /**
     * 获取文件列表
     * @param parentId
     * @param types
     * @return
     */
    @GetMapping("/file/list")
    public List<SysAttachment> getList(@RequestParam("parentId")Long parentId, @RequestParam("types")String types);

    /**
     * 更新文件
     * @param types 模块
     * @param parentId 父级id
     * @param path 路径
     * @return
     */
    @GetMapping("/delete_update")
    public Res update(@RequestParam("types")String types,@RequestParam("parentId") Long parentId,@RequestParam(value = "path",required = false) String path);

}
