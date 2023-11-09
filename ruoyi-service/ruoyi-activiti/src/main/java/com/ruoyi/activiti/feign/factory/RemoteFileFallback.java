package com.ruoyi.activiti.feign.factory;

import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.feign.UrlVo;
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
            public List<Map<String, Object>> getFileUrls(UrlVo urlVo) {
                return null;
            }

            @Override
            public int update(Long parentId, String tempId) {
                return 0;
            }

            @Override
            public int updateMongo(String parentId, String tempId) {
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
             * @param parentId
             * @param types
             * @return
             */
            @Override
            public List<SysAttachment> getListMongo(String parentId, String types) {
                return null;
            }


        };


    }
}
