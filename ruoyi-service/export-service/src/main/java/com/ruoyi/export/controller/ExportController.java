package com.ruoyi.export.controller;

import com.ruoyi.export.utils.PdfUtil;
import com.ruoyi.export.utils.WordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author cxhello
 * @date 2021/4/2
 */
@Controller
public class ExportController{
    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);


    @GetMapping("/exportPdf")
    public void exportPdf(HttpServletResponse response, Map<String, Object> map) {
        File file = null;
        try {
            file = WordUtil.generateWord("img.ftl", map);
            PdfUtil.covertDocToPdf(file, "标准报告", response);
            if (file.exists()) {
                byte[] data = null;
                try {
                    FileInputStream input = new FileInputStream(file);
                    data = new byte[input.available()];
                    input.read(data);
                    response.getOutputStream().write(data);
                    input.close();
                } catch (Exception e) {
                    logger.error("文件转换",e);
                }
            } else {
                return;
            }
        } catch (IOException e) {
            logger.error("1、生成word文档失败");
        } catch (Exception e ){
            logger.error("2、生成PDF失败",e);
        }

    }

    @GetMapping("/exportWord")
    public void exportWord(HttpServletResponse response, Map<String, Object> map) {
        try {
            WordUtil.exportWord("img.ftl", map,"测试",response);
        } catch (Exception e ){
            logger.error("生成word失败",e);
        }

    }

}
