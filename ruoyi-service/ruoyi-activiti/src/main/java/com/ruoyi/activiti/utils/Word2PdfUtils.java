package com.ruoyi.activiti.utils;

import com.ruoyi.common.utils.file.FileUtils;
import lombok.SneakyThrows;
import org.apache.commons.exec.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.Semaphore;

/**
 * word转换pdf工具类
 *
 * @author hjy
 * @date 2023/5/6 15:22
 */
public class Word2PdfUtils {

    /**
     * 转换PDF并以文件流形式返回
     *
     * @param doc      word文件
     * @param fileName 文件名
     * @param response 响应
     */
    public static void toPdfDownload(XWPFDocument doc, String fileName, HttpServletResponse response) {
        String path = "";
        //获取系统标识
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            path = "D:/d/";
        } else {
            path = "/home/minio/training/tmp/";
        }
        String wordPath = path + fileName + ".docx";
        String pdfPath = path + fileName + ".pdf";
        try {
            FileOutputStream fos = new FileOutputStream(wordPath);
            //将word写入到本地
            doc.write(fos);
            fos.close();
            doc.close();
            //转换pdf
            convert(wordPath, path, osName);
            //返回文件流
            response.setContentType("application/pdf;name=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            FileUtils.writeBytes(pdfPath, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //删除生成的word文件
            FileUtils.deleteFile(wordPath);
            //删除生成的PDF文件
            FileUtils.deleteFile(pdfPath);
        }
    }


    /**
     * 转化pdf文件
     *
     * @param filePath       文件地址
     * @param targetFilePath 文件路径
     * @param osName         系统标识
     */
    @SneakyThrows
    private static void convert(String filePath, String targetFilePath, String osName) {
        DefaultExecutor exec = new DefaultExecutor();
        //设置一分钟超时
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
        exec.setWatchdog(watchdog);
        // 同步等待
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        ExecuteResultHandler erh = new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int i) {
                semaphore.release();
                //转换完成逻辑
                System.out.println("转换完成 ！");
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                semaphore.release();
                //转换失败逻辑
                e.printStackTrace();
            }
        };
        //选择执行命令
        String command = "";
        if (osName.contains("Windows")) {
            command = "soffice --invisible --convert-to pdf " + "\"" + filePath + "\"" + " --outdir " + "\"" + targetFilePath + "\"";
        } else {
            String outDir = targetFilePath.length() > 0 ? " --outdir " + targetFilePath : "";
            command = "libreoffice --invisible --convert-to pdf:writer_pdf_Export " + filePath + outDir;
        }
        //执行命令
        exec.execute(CommandLine.parse(command), erh);
        // 等待执行完成
        semaphore.acquire();
    }

    /**
     * word特殊表单单元格行合并
     *
     * @param table    表格
     * @param col      合并行所在列
     * @param startRow 开始行
     */
    public static void mergeCellsVertically(XWPFTable table, int col, int startRow) {
        String tempStr = "";
        int size = table.getRows().size();
        for (int i = startRow; i < size; i++) {
            XWPFTableRow row = table.getRow(i);
            //指定列
            XWPFTableCell cell = row.getCell(col);
            //开始合并
            if (i == startRow || !tempStr.equals(cell.getText())) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                tempStr = cell.getText();
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

}
