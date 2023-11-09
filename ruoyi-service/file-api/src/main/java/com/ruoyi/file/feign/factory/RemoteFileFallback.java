package com.ruoyi.file.feign.factory;

import com.ruoyi.file.domain.Res;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.domain.vo.UrlVo;
import com.ruoyi.file.feign.RemoteFileService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 文件服务
 * @author zx
 */
@Slf4j
@Component
public class RemoteFileFallback implements FallbackFactory<RemoteFileService> {

    @Override
    public RemoteFileService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteFileService() {
            @Override
            public  int updateByIds(List<Long> ids,String types ){ return 0;}

            @Override
            public List<SysAttachment> getListByIds(String ids,String types ){return null; }

            @Override
            public String getFileUrls(String bucketName, String path) {
                return null;
            }

            @Override
            public int update(Long parentId, String tempId) {
                return 0;
            }

            /**
             * 获取文件列表
             *
             * @param parentId
             * @param types
             * @return
             */
            @Override
            public List<SysAttachment> getList(Long parentId, String types) {
                return null;
            }

            /**
             * 获取文件列表
             *
             * @param tempId
             * @param types
             * @return
             */
            @Override
            public List<SysAttachment> getListByTempId(String tempId, String types) {
                return null;
            }

            /**
             * 删除文件
             *
             * @param types 桶
             * @param path  路径 如:“aaa/aaa.jpg”
             * @return
             */
            @Override
            public Res delete(String types, String path) {
                return new Res().setCode(500).setMessage("文件服务连接超时");
            }


        };


    }
}
