package com.ruoyi.training.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * word转换pdf工具类
 *
 * @author hjy
 * @date 2022/6/30 13:12
 */
public class Word2PdfUtils {

    public static void toPdfDownload(XWPFDocument doc, String fileName, HttpServletResponse response) {
        String path = "";
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            path = "D:/d/";
        } else {
            path = "/home/minio/training/tmp/";
        }
        String wordPath = path + fileName + ".docx";
        String pdfPath = path + fileName + ".pdf";
        try {
            response.setContentType("application/pdf;name=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            FileOutputStream fos = new FileOutputStream(wordPath);
            doc.write(fos);
            fos.close();
            doc.close();
            //延迟0.1秒
            Thread.sleep(100);
            int status = toPdf(wordPath, path);
            //程序延迟1.5秒 等待pdf生成
            Thread.sleep(3000);
            //删除生成的word文件
            File wordFile = new File(wordPath);
            if (wordFile.exists()) {
                wordFile.delete();
            }
            FileInputStream in;
            OutputStream out;
            if (status == 0) {
                File file = new File(pdfPath);
                if (file.exists()) {
                    in = new FileInputStream(file);
                    out = response.getOutputStream();
                    byte[] b = new byte[1024 * 5];
                    int n;
                    while ((n = in.read(b)) != -1) {
                        out.write(b, 0, n);
                    }
                    out.flush();
                    in.close();
                    out.close();
                    file.delete();
                }else{
                    System.out.println("文件暂未生成！");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int toPdf(String filePath, String targetFilePath) {
        String command;
        int exitStatus = 1;
        String osName = System.getProperty("os.name");
        String outDir = targetFilePath.length() > 0 ? " --outdir " + targetFilePath : "";
        if (osName.contains("Windows")) {
            command = "cmd /c start soffice --headless --invisible --convert-to pdf:writer_pdf_Export " + "\"" + filePath + "\"" + " --outdir " + "\"" + targetFilePath + "\"";
        } else {
            command = "libreoffice7.1 --headless --invisible --convert-to pdf:writer_pdf_Export " + filePath + outDir;
        }
        try {
            exitStatus = executed(command);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return exitStatus;
    }

    /**
     * 调用操作系统的控制台，执行 command 指令
     * 执行该方法时，并没有等到指令执行完毕才返回，而是执行之后立即返回，返回结果为 0，只能说明正确的调用了操作系统的控制台指令，
     * 但执行结果如何，是否有异常，在这里是不能体现的，所以，更好的姿势是用同步转换功能。
     */
    private static int executed(String command) throws Exception {
        Process process;
        // 转换需要时间，比如一个 3M 左右的文档大概需要 8 秒左右，但实际测试时，并不会等转换结束才执行下一行代码，而是把执行指令发送出去后就立即执行下一行代码了。
        process = Runtime.getRuntime().exec(command);
        int exitStatus = process.waitFor();
        if (exitStatus == 0) {
            exitStatus = process.exitValue();
        }
        // 销毁子进程
        process.destroy();
        return exitStatus;
    }


}
