package com.ruoyi.activiti.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * excle导出
 *
 * @author zh
 * @date 2022/02/25
 */
@Slf4j
public class FileUtil {

    /**
     * 生成excel文件
     *
     * @param
     * @throws Exception
     */
    public static void productionExcel1(Map<String, Object> map, String inPath, String outPath) throws Exception {


        InputStream inputstream = new FileInputStream(inPath);
        OutputStream fileOutputStream = new FileOutputStream(outPath);
        Context context = new Context();
        if (!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<String, Object> data : map.entrySet()) {
                context.putVar(data.getKey(), data.getValue());
            }
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false).setEvaluateFormulas(true).processTemplate(inputstream, fileOutputStream, context);
        }

        if (inputstream != null) {
            try {
                inputstream.close();

            } catch (IOException e) {
                log.error("生成excel关闭输入流", e);
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                log.error("生成excel关闭输出流", e);
            }
        }

    }


    /**
     * 生成excel文件
     *
     * @param
     * @param map
     * @throws Exception
     */
    public static void productionExcel(Map<String, Map<String, Map<String, BigDecimal>>> map, String inPath, String outPath, HttpServletResponse response) throws Exception {


        InputStream inputstream = new FileInputStream(inPath);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=GBK");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("预算信息表", "UTF-8") + ".xls");
//        OutputStream fileOutputStream = new FileOutputStream(outPath);
        Context context = new Context();
        if (!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<String, Map<String, Map<String, BigDecimal>>> data : map.entrySet()) {
                context.putVar(data.getKey(), data.getValue());
            }
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false).setEvaluateFormulas(true).processTemplate(inputstream, outputStream, context);
        }

        if (inputstream != null) {
            try {
                inputstream.close();

            } catch (IOException e) {
                log.error("生成excel关闭输入流", e);
            }
        }


        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("生成excel关闭输出流", e);
            }
        }

    }

    /**
     * 导出excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response, Integer type) throws IOException {
        String tempPath = System.getProperty("java.io.tmpdir") + getDateTimeFormat(type) + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        //response为HttpServletResponse对象

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(getDateTimeFormat(type), "utf-8") + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        // 终止后删除临时文件
        file.deleteOnExit();
        writer.flush(out, true);
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public static String getDateTimeFormat(Integer type) {
        String str = "";
        switch (type) {
            case 0:
                str = "车补记录_";
                break;
            case 1:
                str = "付款记录_";
                break;
            case 2:
                str = "用车申请_";
                break;
            case 3:
                str = "待采购清单_";
        }
        str += SystemUtil.getUserNameCn() + "_" + DateUtil.format(new Date(), "yyyyMMdd");
        return str;
    }
}
