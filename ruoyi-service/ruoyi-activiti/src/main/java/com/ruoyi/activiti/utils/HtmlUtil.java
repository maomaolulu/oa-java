package com.ruoyi.activiti.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * html转换工具
 * @author zx
 * @date 2022-07-27 14:13:37
 */
public class HtmlUtil {
    private HtmlUtil() {}

    public static String html2txt(String text) {
        Document document = Jsoup.parse(text);

        Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);

        document.outputSettings(outputSettings);

        document.select("br").append("\\n");

        document.select("p").prepend("\\n");

        document.select("p").append("\\n");

        String newHtml = document.html().replaceAll("\\\\n", "\n");

        String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);

        String result = StringEscapeUtils.unescapeHtml(plainText.trim());
        return result;
    }

    public static void main(String[] args) {
        System.out.println(html2txt("<body><p>您有一条新的待办事项，请及时处理。</p><br>审批编号：<a>TY202203251648213812324</a><br>申请时间：2022-03-25 21:10<br>隶属公司：杭州安联<br>隶属部门：研发部<br>申请内容：测试邮件记录<br>审批结果：<a style='color:#2d8ccc;'>张旦利<a style='color:#FAAD14;'>审批中</a></a><br><br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>"));
    }
}
