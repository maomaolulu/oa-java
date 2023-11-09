package com.ruoyi.system.feign.factory;

import com.ruoyi.system.feign.RemoteFileService;
import com.ruoyi.system.feign.Res;
import com.ruoyi.system.feign.SysAttachment;
import com.ruoyi.system.feign.UrlVo;
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

            /**
             * 获取url永久
             *
             * @param bucketName
             * @param path
             * @return
             */
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
             * 更新文件
             *
             * @param types
             * @param parentId
             * @param path
             * @return
             */
            @Override
            public Res update(String types, Long parentId, String path) {
                Res res = new Res();
                res.setCode(500);
                res.setMessage("请求超时");
                return res;
            }


        };


    }
}
