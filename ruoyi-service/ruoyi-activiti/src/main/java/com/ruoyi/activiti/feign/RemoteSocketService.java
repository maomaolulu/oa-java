package com.ruoyi.activiti.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件 Feign服务层
 * 
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = "socket-io")
public interface RemoteSocketService
{
    @PostMapping("/push/message")
    String ing(@RequestBody MessageEntity message);

    @PostMapping("/push/cc")
    String cc(@RequestBody MessageEntity message);
    /**
     * 获取在线用户
     * @return
     */
    @PostMapping("/push/users")
    List<String> getUsers();

    /**
     * 发送个推单推通知消息
     * @param cid 用户唯一id
     * @param title 标题
     * @param body 内容
     * @param payload 不展示通知栏的额外信息
     * @return
     */
    @PostMapping("/uni/single")
    boolean singlePush(@RequestParam("cid") String cid,@RequestParam("title")String title,@RequestParam("body")String body,@RequestParam("payload")String payload);

    /**
     * 发送个推单推通知消息
     * @param cid 用户唯一id
     * @param content 透传内容
     * @return
     */
    @PostMapping("/uni/transmission")
    void singlePushTransmission(@RequestParam("cid")String cid,@RequestParam("content")String content);
}
