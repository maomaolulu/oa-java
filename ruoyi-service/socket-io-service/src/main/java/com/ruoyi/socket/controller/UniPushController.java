package com.ruoyi.socket.controller;

import com.ruoyi.socket.service.UniPushService;
import com.ruoyi.socket.utils.UniPushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("uni")
/**
 * 个推接口
 * @author zx
 * @date 2022-07-26 14:35:08
 */
@Slf4j
public class UniPushController {
    private final UniPushService uniPushService;
    @Autowired
    public UniPushController(UniPushService uniPushService) {
        this.uniPushService = uniPushService;
    }

    @PostMapping("single")
    public Boolean singlePush(String cid,String title,String body,String payload){
        try {
           return UniPushUtil.singleUniPush(cid,title,body,payload);
        }catch (Exception e){
            log.error("cid单推通知消息失败",e);
            return false;
        }
    }

    @PostMapping("transmission")
    public void singlePush(String cid,String content){
        try {
            uniPushService.singleTransmission(cid,content);
        }catch (Exception e){
            log.error("cid单推透传消息失败",e);
        }
    }

}
