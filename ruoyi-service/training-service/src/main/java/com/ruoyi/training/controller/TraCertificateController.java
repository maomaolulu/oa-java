package com.ruoyi.training.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.training.entity.TraCertificate;
import com.ruoyi.training.service.TraCertificateService;
import com.ruoyi.training.utils.CertificateDataUtils;
import com.ruoyi.training.utils.Word2PdfUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 培训证书controller
 *
 * @author hjy
 * @date 2022/6/21 16:54
 */
@RestController
@RequestMapping("/training/certificate")
public class TraCertificateController extends BaseController {

    private final TraCertificateService certificateService;

    public TraCertificateController(TraCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * 获取证书列表
     */
    @GetMapping("/list")
    public R list(TraCertificate certificate) {
        try {
            return R.ok("success", certificateService.selectTraCertificateList(certificate));
        } catch (Exception e) {
            logger.error("查询证书列表失败，异常信息：" + e);
            return R.error("查询证书列表失败");
        }
    }

    /**
     * 获取证书详情
     */
    @GetMapping("/getCertificateInfo")
    public R getCertificateInfo(String id) {
        try {
            return R.ok("success", certificateService.getCertificateInfo(id));
        } catch (Exception e) {
            logger.error("查询证书详情失败，异常信息：" + e);
            return R.error("查询证书详情失败");
        }
    }

    /**
     * 证书下载
     */
    @GetMapping("/downloadCertificate")
    public void downloadCertificate(String id, HttpServletResponse response) {
        TraCertificate certificate = certificateService.getCertificateInfo(id);
        int size = certificate.getCourseList().size();
        HashMap<String, Object> map = CertificateDataUtils.packageData(certificate);
        try {
            XWPFDocument doc;
            if (size < 6) {
                doc = WordExportUtil.exportWord07("word/template1.docx", map);
            } else {
                doc = WordExportUtil.exportWord07("word/template2.docx", map);
            }
            Word2PdfUtils.toPdfDownload(doc, certificate.getCertificateName(), response);
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("测试文档.docx", "UTF-8"));
//            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//            doc.write(response.getOutputStream());
//            doc.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


}
