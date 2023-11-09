package com.ruoyi.socket.channel;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.undertow.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageEventHandler {
    @Resource
    private ClientCache clientCache;
    public static SocketIOServer socketIoServer;
    private Map<UUID, String> clientMap = new ConcurrentHashMap<>(16);

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        MessageEventHandler.socketIoServer = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {

        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        UUID sessionId = client.getSessionId();
        System.out.println("用户"+userId+"建立连接---"+sessionId+"---"+ DateUtils.toDateString(new Date()));
        clientCache.saveClient(userId,sessionId,client);

        //获取SessionId
        UUID socketSessionId = client.getSessionId();
        //获取Ip
        String ip = client.getRemoteAddress().toString();
        //添加到客户端map
        clientMap.put(socketSessionId, ip);
        //发送客户端ip给前端
        client.sendEvent("getip", ip);
        //发送上线通知给所有用户
//        clientMap.forEach((k, v) -> socketIoServer.getClient(k).sendEvent("refresh", clientMap.values()));
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        System.out.println("关闭连接---"+client.getSessionId()+"---"+ DateUtils.toDateString(new Date()));
        //获取SessionId
        UUID socketSessionId = client.getSessionId();
        //获取Ip
        String ip = client.getRemoteAddress().toString();
        //从客户端map中移除
        clientMap.remove(socketSessionId);
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        clientCache.deleteSessionClient(userId,socketSessionId);
        //发送下线通知给所有用户
//        clientMap.forEach((k, v) -> socketIoServer.getClient(k).sendEvent("refresh", clientMap.values()));
    }

    @OnEvent("message")
    public void message(SocketIOClient client, AckRequest request, MessageEntity message) {
//        message.setMessage(client.getRemoteAddress().toString());
//        System.out.println(message.getMessage()+"---"+message.getUserId());
//        clientMap.forEach((k, v) -> socketIoServer.getClient(k).sendEvent("receiveMsg", message));
    }
}
