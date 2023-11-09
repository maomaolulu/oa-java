package com.ruoyi.file.feign;

import com.ruoyi.file.domain.Res;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.domain.vo.UrlVo;
import com.ruoyi.file.feign.factory.RemoteFileFallback;
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
     * ids临时文件转有效文件
     * @param ids
     * @param types
     * @return
     */
    @PostMapping("/file/updateByIds")
    public  int updateByIds(@RequestParam("ids") List<Long> ids, @RequestParam("types") String types);

    /**
     * ids获取有效文件
     * @param ids
     * @param types
     * @return
     */
    @GetMapping("/file/getListByIds")
    public List<SysAttachment> getListByIds(@RequestParam(value = "ids") String ids, @RequestParam(name = "types") String types);

    @SneakyThrows(Exception.class)
    @GetMapping("/file/getUrls")
    public String getFileUrls(@RequestParam("bucketName")String bucketName,@RequestParam("path") String path);

    @PostMapping("/file/update")
    public int update(@RequestParam("parentId")Long parentId, @RequestParam(name = "tempId") String tempId);

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
     *
     * @param tempId
     * @param types
     * @return
     */
    @GetMapping("/file/list_temp")
    public List<SysAttachment> getListByTempId(@RequestParam("tempId") String tempId, @RequestParam("types") String types);

    /**
     * 删除文件
     *
     * @param types 桶
     * @param path  路径 如:“aaa/aaa.jpg”
     * @return
     */
    @GetMapping("/file/delete")
    public Res delete(@RequestParam("types") String types,@RequestParam("path")  String path);


}
