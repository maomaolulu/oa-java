package com.ruoyi.file.config.cron;

import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.service.SysAttachmentService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * 文件定时任务
 * @author zx
 * @date 2022-5-18 22:09:48
 */
@Slf4j
public class FileCronService {

    @Transactional(rollbackFor = Exception.class)
    public void deleteTempFile(){
        System.out.println("开始执行删除临时文件");
        SysAttachmentService attachmentService = SpringUtil.getBean(SysAttachmentService.class);
        MinioClient minioClient = SpringUtil.getBean(MinioClient.class);
        try {
            List<SysAttachment> tempList = attachmentService.getTempList();
            for (SysAttachment attachment : tempList) {
                System.out.println("删除临时文件："+attachment.getTypes()+"---"+attachment.getUrl()+"---"+attachment.getTempId());
                String types = attachment.getTypes();
                String url = attachment.getUrl();
                attachmentService.delete(types, url);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(types).object(url).build());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

}