package com.ruoyi.daily.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * excle导出
 * @author zh
 * @date 2022/02/25
 */
public class FileUtil {
    /**
     * 导出excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response,String name) throws IOException {
        String tempPath =System.getProperty("java.io.tmpdir") + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer= ExcelUtil.getBigWriter(file);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        //response为HttpServletResponse对象
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
//        response.setHeader("Content-Disposition","attachment;filename=file.xlsx");
        String fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ServletOutputStream out=response.getOutputStream();
        // 终止后删除临时文件
        file.deleteOnExit();
        writer.flush(out, true);
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }
}
