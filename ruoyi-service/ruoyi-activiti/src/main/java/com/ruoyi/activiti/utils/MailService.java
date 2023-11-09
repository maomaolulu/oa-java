package com.ruoyi.activiti.utils;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.properties.MailProperties;
import com.ruoyi.common.annotation.MailRecordFilter;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * 邮件服务
 * @author zx
 * @date 2022/2/28 18:23
 */
@Service
@EnableAsync
@Slf4j
public class MailService {
    private final MailProperties mailProperties;
    private final RemoteUserService userService;
    private final RemoteSocketService socketService;
    @Autowired
    public MailService(MailProperties mailProperties, RemoteUserService userService, RemoteSocketService socketService) {
        this.mailProperties = mailProperties;
        this.userService = userService;
        this.socketService = socketService;
    }


    /**
     * 初始化邮箱配置
     * @return
     */
    public SendMailUtils initMail(){
        SendMailUtils test = new SendMailUtils();
        test.setInitData(mailProperties.getHost(), mailProperties.getUsername(),mailProperties.getPassword());
        return test;
    }

    /**
     * 异步发送邮件和app通知
     * @param text
     * @param subject
     * @param email
     * @throws MessagingException
     */
    @Async
    @MailRecordFilter
    public Boolean send(String text,String subject,String email,String createBy,String cid){

        System.out.println(cid);

        if(StrUtil.isNotBlank(cid)){
            boolean b = socketService.singlePush(cid, subject, HtmlUtil.html2txt(text),null);
            if(b){
                log.info("消息通知发送成功");
            }else {
                log.error(cid+"消息通知发送失败");
            }
        }
        SendMailUtils test = initMail();
        boolean flag = false;
        try {
            test.richContentSend(email, subject, text, null);
            flag = true;
        }catch (Exception e){
            log.info(email+"发送邮件失败",e);
        }finally {
            return flag;
        }


    }
}
