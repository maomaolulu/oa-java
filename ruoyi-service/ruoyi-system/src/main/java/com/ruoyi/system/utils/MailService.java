package com.ruoyi.system.utils;

import com.ruoyi.system.domain.government_centre.BizMailRecord;
import com.ruoyi.system.feign.RemoteMailRecordService;
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
    private final RemoteUserService remoteUserService;
    private final RemoteMailRecordService mailRecordService;
    @Autowired
    public MailService(MailProperties mailProperties, RemoteUserService remoteUserService, RemoteMailRecordService mailRecordService) {
        this.mailProperties = mailProperties;
        this.remoteUserService = remoteUserService;
        this.mailRecordService = mailRecordService;
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
     * 异步发送邮件
     * @param text
     * @param subject
     * @param email
     * @throws MessagingException
     */
    @Async
    public void send(String text,String subject,String email) throws MessagingException {
        SendMailUtils test = initMail();
        test.richContentSend(email, subject, text, null);
    }

    /**
     * 保存敏感邮件
     * @param text
     * @param subject
     * @param email
     */
    @Async
    public void sendSecret(String text,String subject,String email,String createBy) {
        BizMailRecord bizMailRecord = new BizMailRecord();
        bizMailRecord.setContent(text);
        bizMailRecord.setCreateBy(createBy);
        bizMailRecord.setSubject(subject);
        bizMailRecord.setSendTo(email);
        mailRecordService.save(bizMailRecord);
    }

}
