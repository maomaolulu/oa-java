package com.ruoyi.file.controller;

import com.ruoyi.file.domain.Res;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
/**
 * 文件服务new
 */
@Slf4j
@RestController
@RequestMapping("fileNew")
public class MinioNewController {

    @Resource
    private MinioClient minioClient;

    /**
     * 上传节日图片
     *
     * @param
     * @param
     * @return
     *
     * @throws Exception
     */
    @PostMapping(value = "/uploadNewAndOld")
    public Res uploadNewAndOld(@RequestParam(name = "file") MultipartFile file, String type, String name) {

        Res res = new Res();


        if (file == null || file.getSize() == 0) {
            res.setCode(500);
            res.setMessage("上传文件不能为空");
            return res;
        }


        try {

            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String originFileName = file.getOriginalFilename();
            String suffix = originFileName.substring(originFileName.lastIndexOf("."));
            String newFileName = type+name+suffix;
            log.info(type+"--"+name+"--"+suffix);
            InputStream in = file.getInputStream();
            PutObjectArgs args = PutObjectArgs.builder()
                    //路径
                    .bucket("oa-static")
                    //文件名
                    .object(newFileName)
                    //流
                    .stream(in, file.getSize(), -1)
                    //后缀
                    .contentType(file.getContentType())
                    .build();
            // 没有bucket则创建
            boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket("oa-static").build());
            if (!exist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("oa-static").build());
            }
            minioClient.putObject(args);
            in.close();
            return res.setCode(200).setMessage("上传成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setCode(500);
            res.setMessage("上传失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return res;
        }
    }

}
