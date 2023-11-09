package com.ruoyi.file.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.OaConstants;
import com.ruoyi.file.domain.*;
import com.ruoyi.file.utils.CommandService;
import com.ruoyi.file.service.SysAttachmentService;
import com.ruoyi.file.service.SysStaticImgService;
import com.ruoyi.file.utils.PinYinUtil;
import com.ruoyi.file.utils.QrCodeUtils;
import com.ruoyi.system.util.SystemUtil;
import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.common.core.domain.R.data;

/**
 * 文件服务
 *
 * @menu 文件服务
 */
@Slf4j
@RestController
@RequestMapping("file")
public class MinioController {
    @Resource
    private MinioClient minioClient;

    private final SysAttachmentService attachmentService;
    private final SysStaticImgService staticImgService;
    private final CommandService commandService;

    @Autowired
    public MinioController(SysAttachmentService attachmentService, SysStaticImgService staticImgService, CommandService commandService) {
        this.attachmentService = attachmentService;
        this.staticImgService = staticImgService;
        this.commandService = commandService;
    }

    @Value("${video_file_path}")
    private String fileStorePath;

    @PostConstruct
    private void vt() {
        System.out.println(fileStorePath);
    }

    /**
     * 校验分片文件
     *
     * @param fileMd5
     * @Title: 判断文件是否上传过，是否存在分片，断点续传
     * @MethodName: checkBigFile
     * @Return com.lovecyy.file.up.example3.vo.JsonResult
     * @Exception
     * @Description: 文件已存在，下标为-1
     * 文件没有上传过，下标为零
     * 文件上传中断过，返回当前已经上传到的下标
     */
    //  /home/minio/oa-static/weixin/
    @PostMapping(value = "/check")
    public R checkBigFile(String fileMd5) {
        // 秒传
        File mergeMd5Dir = new File(fileStorePath + "/" + "merge" + "/" + fileMd5);
        if (mergeMd5Dir.exists()) {
            mergeMd5Dir.mkdirs();
            return data(-1);
        }
        // 读取目录里的所有文件
        File dir = new File(fileStorePath + "/" + fileMd5);
        File[] childs = dir.listFiles();
        // 文件没有上传过，下标为零
        int code = 0;
        if (childs != null) {
            // 文件上传中断过，返回当前已经上传到的下标
            code = childs.length - 1;
        }
        return data(code);
    }

    /**
     * 上传分片文件
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/partial_upload")
    public void filewebUpload(MultipartFileParam param, HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        // 文件名
        String fileName = param.getName();
        // 文件每次分片的下标
        int chunkIndex = param.getChunk();
        if (isMultipart) {
            File file = new File(fileStorePath + "/" + param.getMd5());
            if (!file.exists()) {
                file.mkdir();
            }
            File chunkFile = new File(
                    fileStorePath + "/" + param.getMd5() + "/" + chunkIndex);
            try {
                FileUtils.copyInputStreamToFile(param.getFile().getInputStream(), chunkFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("文件-:{}的小标-:{},上传成功", fileName, chunkIndex);
        return;
    }

    /**
     * 分片上传成功之后，合并文件
     *
     * @param fileName
     * @param fileMd5
     * @return
     */
    @PostMapping(value = "/merge")
    @ResponseBody
    public R filewebMerge(String fileName, String fileMd5) {
        FileChannel outChannel = null;
        try {
            // 读取目录里的所有文件
            File dir = new File(fileStorePath + "/" + fileMd5);
            File[] childs = dir.listFiles();
            if (Objects.isNull(childs) || childs.length == 0) {
                return null;
            }
            // 转成集合，便于排序
            List<File> fileList = new ArrayList<>(Arrays.asList(childs));
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                        return -1;
                    }
                    return 1;
                }
            });
            // 合并后的文件
            File outputFile = new File(fileStorePath + "/" + "merge" + "/" + fileMd5 + "/" + fileName);
            // 创建文件
            if (!outputFile.exists()) {
                File mergeMd5Dir = new File(fileStorePath + "/" + "merge" + "/" + fileMd5);
                if (!mergeMd5Dir.exists()) {
                    mergeMd5Dir.mkdirs();
                }
                log.info("创建文件");
                outputFile.createNewFile();
            }
            outChannel = new FileOutputStream(outputFile).getChannel();
            FileChannel inChannel = null;
            try {
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inChannel.close();
                    // 删除分片
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //发生异常，文件合并失败 ，删除创建的文件
                outputFile.delete();
                dir.delete();//删除文件夹
            } finally {
                if (inChannel != null) {
                    inChannel.close();
                }
            }
            dir.delete(); //删除分片所在的文件夹

            // 处理视频元数据
            String cmdLog2 = commandService.executeCmd("qt-faststart " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/" + fileName + " " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/fast" + fileName);
            log.info("视频元数据处理完毕");
            File dirNew = new File(fileStorePath + "/merge/" + fileMd5);
            File[] childsNew = dirNew.listFiles();
            System.out.println(childsNew.length);
            if (childsNew.length < 2) {
                // 若元数据损坏，则重新转码
                String cmdLog1 = commandService.executeCmd("ffmpeg -i " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/" + fileName
                        + " -acodec copy -vcodec copy "
                        + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/test" + fileName);
                log.info("视频转码完毕");
                String cmdLog4 = commandService.executeCmd("qt-faststart " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/test" + fileName + " " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/fast" + fileName);
                log.info("视频元数据二次处理完毕");
                // 删除转码文件
                String cmdLog3 = commandService.executeCmd("rm -rf " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/test" + fileName);
                log.info("删除转码文件完毕");
            }

            // 删除原视频
            String cmdLog3 = commandService.executeCmd("rm -rf " + fileStorePath + "/" + "merge" + "/" + fileMd5 + "/" + fileName);
            log.info("删除原视频完毕");
            // FIXME: 数据库操作, 记录文件存档位置
            SysAttachment attachment = new SysAttachment();
            attachment.setDelFlag("2");
            attachment.setName("fast" + fileName);
            attachment.setTypes("training");
            attachment.setTempId(fileMd5);
            attachment.setUrl("merge/" + fileMd5 + "/");
            attachmentService.save(attachment);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("合并失败");
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return R.ok("合并成功");
    }


    @GetMapping("/test")
    public String test(String bucketName, String path) {
        return getFileUrls(bucketName, path);
    }


/*        @GetMapping("/list")
    public List<Object> list(ModelMap map) throws Exception {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(MINIO_BUCKET);
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
        }
        return items;
    }*/

    /**
     * 临时文件转有效文件
     *
     * @param tempId
     * @return
     */
    @PostMapping("/update")
    public int update(@RequestParam(value = "parentId", required = false) Long parentId, @RequestParam(name = "tempId") String tempId) {

        return attachmentService.update(parentId, tempId);
    }

    /**
     * 临时文件转有效文件
     *
     * @param tempId
     * @return
     */
    @PostMapping("/update_m")
    public int update(@RequestParam(value = "parentId", required = false) String parentId, @RequestParam(name = "tempId") String tempId) {

        return attachmentService.update(parentId, tempId);
    }

    /**
     * 获取文件列表
     *
     * @param parentId
     * @param types
     * @return
     */
    @GetMapping("/list")
    public List<SysAttachment> getList(@RequestParam("parentId") Long parentId, @RequestParam("types") String types) {
        List<SysAttachment> list = attachmentService.getList(types, parentId);
        for (SysAttachment attachment : list) {
            attachment.setPreUrl(getFileUrl(types, attachment.getUrl()));
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 临时文件转有效文件
     *
     * @param ids
     * @param types
     * @return
     */
    @PostMapping("/updateByIds")
    public int updateByIds(@RequestParam(value = "ids") List<Long> ids, @RequestParam(name = "types") String types) {

        return attachmentService.updateByIds(ids, types);
    }

    /**
     * 获取文件列表
     *
     * @param ids
     * @param types
     * @return
     */
    @GetMapping("/getListByIds")
    public List<SysAttachment> getListByIds(@RequestParam("ids") String ids, @RequestParam("types") String types) {
        String[] split = ids.split(",");
        ArrayList<Long> longs = new ArrayList<>();
        for (String s : split
        ) {
            longs.add(Long.valueOf(s));
        }
        List<SysAttachment> list = attachmentService.getListByIds(longs, types);
        for (SysAttachment attachment : list) {
            attachment.setPreUrl(getFileUrl(types, attachment.getUrl()));
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 获取Mongo文件列表
     *
     * @param parentId
     * @param types
     * @return
     */
    @GetMapping("/list_m")
    public List<SysAttachment> getListMongo(@RequestParam("parentId") String parentId, @RequestParam("types") String types) {
        List<SysAttachment> list = attachmentService.getListMongo(types, parentId);
        for (SysAttachment attachment : list) {
            attachment.setPreUrl(getFileUrl(types, attachment.getUrl()));
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 获取文件列表
     *
     * @param tempId
     * @param types
     * @return
     */
    @GetMapping("/list_temp")
    public List<SysAttachment> getListByTempId(@RequestParam("tempId") String tempId, @RequestParam("types") String types) {
        List<SysAttachment> list = attachmentService.getListByTempId(types, tempId);
        for (SysAttachment attachment : list) {
            attachment.setPreUrl(getFileUrls(types, attachment.getUrl() + attachment.getName()));
        }
        if (list == null) {
            list = new ArrayList<>();
        }
//        String s = commandService.executeCmd("test.sh");
//        log.error(s);

        return list;
    }


    /**
     * 上传文件
     *
     * @param file     文件数组
     * @param types    桶(文件所属模块)
     * @param path     文件夹 "company"+ 公司id +“/” 如:“company115/”
     * @param tempId   临时唯一标识
     * @param parentId 父级主键
     * @param delFlag  0有效1删除2临时
     * @return
     */
    @PostMapping("/upload")
    @OperLog(title = "上传文件", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public Res upload(@RequestParam(name = "file", required = false) MultipartFile[] file, @RequestParam(name = "path") String path, @RequestParam(name = "tempId", required = false) String tempId
            , @RequestParam(name = "parentId", required = false) Long parentId, @RequestParam(name = "types") String types, @RequestParam(name = "delFlag") String delFlag) {
        Res res = new Res();
        res.setCode(500);

        if (file == null || file.length == 0) {
            res.setMessage("上传文件不能为空");
            return res;
        }

        List<Map<String, Object>> orgfileNameList = new ArrayList<>(file.length);

        try {
            for (MultipartFile multipartFile : file) {
                Date date = new Date();
                String timestamp = String.valueOf(date.getTime());
                String originFileName = multipartFile.getOriginalFilename();
                String type = originFileName.substring(originFileName.lastIndexOf("."));
                String newFileName = timestamp.concat(type);
                InputStream in = multipartFile.getInputStream();
                PutObjectArgs args = PutObjectArgs.builder()
                        //路径
                        .bucket(types)
                        //文件名
                        .object(path.concat(newFileName))
                        //流
                        .stream(in, multipartFile.getSize(), -1)
                        //后缀
                        .contentType(multipartFile.getContentType())
                        .build();
                // 没有bucket则创建
                boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(types).build());
                if (!exist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(types).build());
                }
                minioClient.putObject(args);
                in.close();
                SysAttachment attachment = new SysAttachment();
                attachment.setDelFlag(delFlag);
                attachment.setName(originFileName);
                attachment.setParentId(parentId);
                attachment.setTypes(types);
                attachment.setTempId(tempId);
                String url = path.concat(newFileName);
                attachment.setUrl(url);
                attachmentService.save(attachment);
                Map<String, Object> map = new HashMap<>(3);
                map.put("url", getFileUrl(types, url));
                map.put("path", url);
                map.put("fileName", originFileName);
                map.put("newFileName", newFileName);
                map.put("id", attachment.getId());
                orgfileNameList.add(map);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setMessage("上传失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return res;
        }

        Map<String, Object> data = new HashMap<String, Object>(1);
        data.put("url", orgfileNameList);
        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(data);

        return res;
    }

    /**
     * 上传文件(mongodb)
     *
     * @param file     文件数组
     * @param types    桶(文件所属模块)
     * @param path     文件夹 "company"+ 公司id +“/” 如:“company115/”
     * @param tempId   临时唯一标识
     * @param parentId 父级主键
     * @param delFlag  0有效1删除2临时
     * @return
     */
    @PostMapping("/upload_m")
    @OperLog(title = "上传文件mongo", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public Res uploadMongodb(@RequestParam(name = "file", required = false) MultipartFile[] file, @RequestParam(name = "path") String path, @RequestParam(name = "tempId", required = false) String tempId
            , @RequestParam(name = "parentId", required = false) String parentId, @RequestParam(name = "types") String types, @RequestParam(name = "delFlag") String delFlag) {
        Res res = new Res();
        res.setCode(500);

        if (file == null || file.length == 0) {
            res.setMessage("上传文件不能为空");
            return res;
        }

        List<Map<String, Object>> orgfileNameList = new ArrayList<>(file.length);

        try {
            for (MultipartFile multipartFile : file) {
                Date date = new Date();
                String timestamp = String.valueOf(date.getTime());
                String originFileName = multipartFile.getOriginalFilename();
                String type = originFileName.substring(originFileName.lastIndexOf("."));
                String newFileName = timestamp.concat(type);
                InputStream in = multipartFile.getInputStream();
                PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(types)
                        .object(path.concat(newFileName))
                        .stream(in, multipartFile.getSize(), -1)
                        .contentType(multipartFile.getContentType())
                        .build();
                // 没有bucket则创建
                boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(types).build());
                if (!exist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(types).build());
                }
                minioClient.putObject(args);
                in.close();
                SysAttachment attachment = new SysAttachment();
                attachment.setDelFlag(delFlag);
                attachment.setName(originFileName);
                attachment.setMParentId(parentId);
                attachment.setTypes(types);
                attachment.setTempId(tempId);
                String url = path.concat(newFileName);
                attachment.setUrl(url);
                attachmentService.save(attachment);
                Map<String, Object> map = new HashMap<>(3);
                map.put("url", getFileUrl(types, url));
                map.put("path", url);
                map.put("fileName", originFileName);
                map.put("newFileName", newFileName);
                map.put("id", attachment.getId());
                orgfileNameList.add(map);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setMessage("上传失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return res;
        }

        Map<String, Object> data = new HashMap<String, Object>(1);
        data.put("url", orgfileNameList);
        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(data);

        return res;
    }

    /**
     * 上传固定资产文件
     *
     * @param file 文件数组
     * @return
     */
    @PostMapping("/upload_asset")
    @OperLog(title = "上传固定资产文件", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public Res upload(@RequestParam(name = "file", required = false) MultipartFile[] file, @RequestParam("assetSn") String assetSn) {
        Res res = new Res();
        res.setCode(500);
        String types = "inventory";
        if (file == null || file.length == 0) {
            res.setMessage("上传文件不能为空");
            return res;
        }
        String path = PinYinUtil.getPinyin(SystemUtil.getCompanyName()) + "/";
        List<Map<String, Object>> orgFileNameList = new ArrayList<>(file.length);
        List<String> urlList = new ArrayList<>(file.length);
        try {
            for (MultipartFile multipartFile : file) {
                Date date = new Date();
                String timestamp = String.valueOf(date.getTime());
                String originFileName = multipartFile.getOriginalFilename();
                String type = originFileName.substring(originFileName.lastIndexOf("."));
                String newFileName = assetSn.concat("_").concat(timestamp.concat(type));
                InputStream in = multipartFile.getInputStream();
                PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(types)
                        .object(path.concat(newFileName))
                        .stream(in, multipartFile.getSize(), -1)
                        .contentType(multipartFile.getContentType())
                        .build();
                // 没有bucket则创建
                boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(types).build());
                if (!exist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(types).build());
                }
                minioClient.putObject(args);
                in.close();
                SysAttachment attachment = new SysAttachment();
                attachment.setDelFlag("2");
                attachment.setName(originFileName);
                attachment.setParentId(null);
                attachment.setTypes(types);
                attachment.setTempId(assetSn);
                String url = path.concat(newFileName);
                attachment.setUrl(url);
                attachmentService.save(attachment);
                Map<String, Object> map = new HashMap<>(4);
                map.put("url", getFileUrl(types, url));
                map.put("path", url);
                map.put("fileName", originFileName);
                map.put("newFileName", newFileName);
                map.put("newPath", types + "/" + path + newFileName);
                orgFileNameList.add(map);
                urlList.add(types + "/" + path + newFileName);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setMessage("上传失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return res;
        }

        Map<String, Object> data = new HashMap<String, Object>(2);
        data.put("info", orgFileNameList);
        data.put("url", String.join(";", urlList));
        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(data);

        return res;
    }

    /**
     * 获取静态文件列表
     *
     * @return
     */
    @GetMapping("/list_static")
    public Res getListStatic() {
        List<SysStaticImg> list = staticImgService.getList();
        for (SysStaticImg img : list) {
            String fileUrl = getFileUrl(img.getTypes(), img.getPath());
            img.setUrl(fileUrl);
        }
        Res res = new Res();
        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(list);
        return res;
    }

    public static void main(String[] args) {
        System.out.println("ss/aa".split("/")[0]);
    }

    /**
     * 下载静态文件
     *
     * @param response
     * @param path
     */
    @GetMapping("/download_static")
    public void download(HttpServletResponse response, @RequestParam(name = "path") String path, @RequestParam(name = "fileName") String fileName) {
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket("oa-static").object(path.concat(fileName)).build());
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            in = minioClient.getObject(GetObjectArgs.builder().bucket("oa-static").object(path.concat(fileName)).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 预览静态文件
     *
     * @param response
     * @param path
     */
    @GetMapping("/pre_static")
    public void preStatic(HttpServletResponse response, @RequestParam(name = "path") String path, @RequestParam(name = "fileName") String fileName) {
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket("oa-static").object(path.concat(fileName)).build());
            response.setContentType(stat.contentType());
            in = minioClient.getObject(GetObjectArgs.builder().bucket("oa-static").object(path.concat(fileName)).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @PostMapping("/upload_static")
    @OperLog(title = "上传静态文件", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public Res uploadStatic(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam(name = "path") String path, @RequestParam(name = "types") String types,
                            @RequestParam(name = "pathNew") String pathNew, @RequestParam(name = "typesNew") String typesNew) {
        Res res = new Res();
        res.setCode(500);

        if (file == null) {
            res.setMessage("上传文件不能为空");
            return res;
        }
        try {
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String originFileName = file.getOriginalFilename();
            InputStream in = file.getInputStream();
            String newFileName = originFileName.split("\\.")[0].concat(timestamp).concat(".").concat(originFileName.split("\\.")[1]);
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(types)
                    .object(path.concat(newFileName))
                    .stream(in, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            // 没有bucket则创建
            boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(types).build());
            if (!exist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(types).build());
            }
            minioClient.putObject(args);

            in.close();
            InputStream in2 = file.getInputStream();
            PutObjectArgs args2 = PutObjectArgs.builder()
                    .bucket(typesNew)
                    .object(pathNew)
                    .stream(in2, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            // 没有bucket则创建
            boolean exist2 = minioClient.bucketExists(BucketExistsArgs.builder().bucket(typesNew).build());
            if (!exist2) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(typesNew).build());
            }
            minioClient.putObject(args2);
            in2.close();

            String url = path.concat(newFileName);
            SysStaticImg img = new SysStaticImg();
            img.setPath(url);
            img.setTypes(types);
            img.setName(originFileName);
            staticImgService.insert(img);
        } catch (Exception e) {
            log.error(e.getMessage());
            res.setMessage("上传失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return res;
        }

        res.setCode(200);
        res.setMessage("上传成功");
        res.setData(null);

        return res;
    }

    @SneakyThrows(Exception.class)
    private String getFileUrl(String bucketName, String path) {
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(path).method(Method.GET).expiry(60, TimeUnit.MINUTES).build());
        System.out.println(url);
        if (url.contains(OaConstants.MINIO_IP_PORT)) {
            url = url.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
        }
        return url;
    }

    /**
     * pdf预览
     *
     * @param path pdf路径
     * @return
     */
    @GetMapping("pre_pdf")
    public String getPreFdf(String path) {
        try {
            return getFileUrl("tmp", path.split("tmp/")[1]);
        } catch (Exception e) {
            log.error("pdf预览", e);
            return "";
        }
    }

    /**
     * 视频链接
     *
     * @param bucketName training
     * @param path       路径
     * @return
     */
    @GetMapping("pre_download")
    public R preDownload(String bucketName, String path) {
        String fileUrl = getFileUrl(bucketName, path);
        return data(fileUrl);
    }

    /**
     * 获取app最新版本信息
     */
    @GetMapping("app")
    public R getAppVersionInfo(String type) {
        try {
            int a = "ios".equals(type) ? 3 : 2;
            SysAppVersion lastAppVersion = attachmentService.getLastAppVersion(a);
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("app").object(lastAppVersion.getWgt()).method(Method.GET).build());
            url = url.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
            System.out.println(url);
            lastAppVersion.setWgt(url);
            return data(lastAppVersion);
        } catch (Exception e) {
            log.error("获取版本更新失败", e);
            return R.error("获取版本更新失败");
        }
    }

    /**
     * 获取app最新安装包信息
     */
    @GetMapping("last_app")
    public R getLastApp() {
        try {
            HashMap<Object, Object> map = new HashMap<>(2);
            String url = "";
            SysAppVersion lastAppVersion = attachmentService.getLastApp(2);
            if (lastAppVersion != null) {
                url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("app").object(lastAppVersion.getWgt()).method(Method.GET).build());
                url = url.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
            }
            map.put("android", url);
            SysAppVersion lastAppVersion2 = attachmentService.getLastApp(3);
            String url2 = "";
            if (lastAppVersion2 != null) {
                url2 = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("app").object(lastAppVersion2.getWgt()).method(Method.GET).build());
                url2 = url2.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
            }
            map.put("ios", url2);
            return data(map);
        } catch (Exception e) {
            log.error("获取安装包失败", e);
            return R.error("获取安装包失败");
        }
    }

    /**
     * 获取app最新安装包二维码
     */
    @GetMapping("last_app_qr")
    public void getLastAppQR(int type, HttpServletResponse response) {
        try {
            SysAppVersion lastAppVersion = attachmentService.getLastApp(type);
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("app").object(lastAppVersion.getWgt()).method(Method.GET).build());
            url = url.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
            System.out.println(url);
            String text = url;
            String logoPath = "/home/minio/tmp/app.jpg";
            QrCodeUtils.encode(text, logoPath, response.getOutputStream(), true);

        } catch (Exception e) {
            log.error("获取安装包二维码失败", e);
            throw new RuntimeException("无此安装包，请联系管理员");
        }
    }


    /**
     * 获取url
     *
     * @param bucketName
     * @return
     */
    @SneakyThrows(Exception.class)
    @GetMapping("/getUrls")
    public String getFileUrls(String bucketName, String path) {

        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(path).method(Method.GET).build());
        if (url.contains(OaConstants.MINIO_IP_PORT)) {
            url = url.replace(OaConstants.MINIO_IP_PORT, "https://coa.anliantest.com/oaProxyApi/proxyJava/minio");
        }
        return url;
    }

    /**
     * 下载
     *
     * @param response
     * @param bucketName
     * @param fileName
     * @param path
     */
    @PostMapping("/download")
    public void download(HttpServletResponse response, @RequestParam(name = "bucketName") String bucketName, @RequestParam(name = "path") String path, @RequestParam(name = "fileName") String fileName) {
        // 打印参数
        System.out.println("bucketName:" + bucketName + " path:" + path + " fileName:" + fileName);
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(path.concat(fileName)).build());

            response.setContentType(stat.contentType());
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(path.concat(fileName)).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 下载
     *
     * @param response
     * @param bucketName
     * @param fileName
     * @param path
     */
    @GetMapping("/download2")
    public void download2(HttpServletResponse response, @RequestParam(name = "bucketName") String bucketName, @RequestParam(name = "path") String path, @RequestParam(name = "fileName") String fileName) {
        // 打印参数
        System.out.println("bucketName:" + bucketName + " path:" + path + " fileName:" + fileName);
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(path.concat(fileName)).build());

            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(path.concat(fileName)).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 预览文件
     *
     * @param response
     * @param bucketName
     * @param path
     */
    @GetMapping("/pre_file")
    public void preFile(HttpServletResponse response, @RequestParam(name = "bucketName") String bucketName, @RequestParam(name = "path") String path) {
        // 打印参数
        System.out.println("bucketName:" + bucketName + " path:" + path);
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(path).build());

            response.setContentType(stat.contentType());
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(path).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param types 桶
     * @param path  路径 如:“aaa/aaa.jpg”
     * @return
     */
    @GetMapping("/delete")
    @OperLog(title = "删除文件", businessType = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Res delete(String types, String path) {
        Res res = new Res();
        res.setCode(200);
        res.setMessage("删除成功");
        try {
            attachmentService.delete(types, path);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(types).object(path).build());
        } catch (Exception e) {
            res.setCode(500);
            res.setMessage("删除失败");
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return res;
    }

    /**
     * 删除静态文件
     *
     * @param types 桶
     * @param path  路径 如:“aaa/aaa.jpg”
     * @return
     */
    @GetMapping("/delete_static")
    @OperLog(title = "删除文件", businessType = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Res deleteStatic(String types, String path) {
        Res res = new Res();
        res.setCode(200);
        res.setMessage("删除成功");
        try {
            staticImgService.delete(types, path);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(types).object(path).build());
        } catch (Exception e) {
            res.setCode(500);
            res.setMessage("删除失败");
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return res;
    }

    /**
     * 更新文件,新文件转有效文件，删除旧文件
     *
     * @param types    桶
     * @param path     路径 如:“aaa/aaa.jpg”
     * @param parentId 父级id
     * @return
     */
    @GetMapping("/delete_update")
    @OperLog(title = "更新文件", businessType = BusinessType.DELETE)
    public Res update(String types, Long parentId, String path) {
        Res res = new Res();
        res.setCode(200);
        res.setMessage("删除成功");
        try {
            // 有路径按路径删
            if (StrUtil.isNotBlank(path)) {
                extractedDelete(types, path);
            } else {
                // 没路径按模块和父级id删旧文件
                List<SysAttachment> list = attachmentService.getList(types, parentId);
                list.stream().filter(f -> "0".equals(f.getDelFlag())).forEach(attachment -> {
                    extractedDelete(attachment.getTypes(), attachment.getUrl());
                });
            }
            // 将新临时文件转有效文件
            update(parentId, null);
        } catch (Exception e) {
            res.setCode(500);
            res.setMessage("更新失败");
            log.error(e.getMessage());
        }
        return res;
    }

    /**
     * 删除文件记录和文件
     *
     * @param types
     * @param path
     */
    @SneakyThrows(Exception.class)
    private void extractedDelete(String types, String path) {
        attachmentService.delete(types, path);
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(types).object(path).build());
    }

    /**
     * 获取报告二维码
     */
    @GetMapping("report")
    public void getLastAppQR(String name, HttpServletResponse response) {
        try {
//            String text = "http://192.168.0.204:8006/file/pre_bg?name="+name;
            String text = "https://coa.anliantest.com/oaProxyApi/proxyJava/file/file/pre_bg?name=" + name;
            String logoPath = "/home/minio/report/ALT.png";
            QrCodeUtils.encode(text, logoPath, response.getOutputStream(), true);

        } catch (Exception e) {
            log.error("获取报告失败", e);
            throw new RuntimeException("无此报告，请联系管理员");
        }
    }

    /**
     * 获取报告
     */
    @GetMapping("/pre_bg")
    public void preBg(HttpServletResponse response, @RequestParam(name = "name") String name) {
//        preFile(response,"report",name);】
//        attachmentService.counter();
//        download2(response,"report","",name);
        InputStream in = null;
        try {

            ObjectStat stat = minioClient.statObject(StatObjectArgs.builder().bucket("report").object(name).build());

            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));

            in = minioClient.getObject(GetObjectArgs.builder().bucket("report").object(name).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            attachmentService.counter();
        }
    }
//    private static String formatFileSize(long fileS) {
//        DecimalFormat df = new DecimalFormat("#.00");
//        String fileSizeString = "";
//        String wrongSize = "0B";
//        if (fileS == 0) {
//            return wrongSize;
//        }
//        if (fileS < 1024) {
//            fileSizeString = df.format((double) fileS) + " B";
//        } else if (fileS < 1048576) {
//            fileSizeString = df.format((double) fileS / 1024) + " KB";
//        } else if (fileS < 1073741824) {
//            fileSizeString = df.format((double) fileS / 1048576) + " MB";
//        } else {
//            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
//        }
//        return fileSizeString;
//    }
}