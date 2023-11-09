package com.ruoyi.socket.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.ruoyi.socket.channel.ClientCache;
import com.ruoyi.socket.channel.MessageEntity;
import io.undertow.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/push")
public class PushController {
    @Resource
    private ClientCache clientCache;
 
    @PostMapping("/message")
    public String pushTuUser(@RequestBody MessageEntity message){
        log.info(message.getMessage()+"-----"+message.getUserId()+"----"+message.getProcDefKey());
        if(StringUtils.isBlank(message.getUserId())){
            return null;
        }
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(message.getUserId());
        if(userClient==null){
            return "用户 "+message.getUserId()+" 未连接："+message.getMessage();
        }
        userClient.forEach((uuid, socketIOClient) -> {
            //向客户端推送消息
            socketIOClient.sendEvent("chatevent",message);
//            clientCache.saveClient(message.getUserId(),uuid,socketIOClient);
            System.out.println("发送给"+uuid+"的消息"+message);
        });
        return message.getMessage();
    }
    @PostMapping("/cc")
    public String pushTuUserCc(@RequestBody MessageEntity message){
        log.info(message.getMessage()+"-----"+message.getUserId()+"----"+message.getProcDefKey());
        if(StringUtils.isBlank(message.getUserId())){
            return null;
        }
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(message.getUserId());
        if(userClient==null){
            return "用户 "+message.getUserId()+" 未连接："+message.getMessage();
        }
        userClient.forEach((uuid, socketIOClient) -> {
            //向客户端推送消息
            socketIOClient.sendEvent("cc_event",message);
        });
        return message.getMessage();
    }

    /**
     * 获取在线用户
     * @return
     */
    @PostMapping("users")
    public List<String> getUsers(){
        log.info("获取在线用户");
        return clientCache.getUsers();
    }

    /**
     * 关闭链接
     * @return
     */
    @GetMapping("dis")
    public Map<String,Object> getUsers(String userId, String sessionId){
        Map<String,Object> map = new HashMap<>(1);
        UUID uuid = UUID.fromString(sessionId);
        try {
            log.info(userId+"主动关闭链接---"+sessionId+"---"+ DateUtils.toDateString(new Date()));
            clientCache.deleteSessionClient(userId,uuid);
            map.put("code",200);
            return map;
        }catch (Exception e){
            map.put("code",200);
            return map;
        }
    }

    @PostMapping("/ing")
    public String pushIng(@RequestBody MessageEntity message){
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(message.getUserId());
        userClient.forEach((uuid, socketIOClient) -> {
            //向候选人推送待办
            socketIOClient.sendEvent("chatevent",message.getMessage());
        });
        return message.getMessage();
    }

}