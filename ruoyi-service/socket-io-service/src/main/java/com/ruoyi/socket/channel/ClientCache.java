package com.ruoyi.socket.channel;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientCache {

    /**
     * 本地缓存
     */
    private static Map<String, HashMap<UUID, SocketIOClient>> concurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 存入本地缓存
     *
     * @param userId         用户ID
     * @param sessionId      页面sessionID
     * @param socketIOClient 页面对应的通道连接信息
     */
    public void saveClient(String userId, UUID sessionId, SocketIOClient socketIOClient) {
        HashMap<UUID, SocketIOClient> sessionIdClientCache = concurrentHashMap.get(userId);
        if (sessionIdClientCache == null) {
            sessionIdClientCache = new HashMap<>(1);
        }
        sessionIdClientCache.put(sessionId, socketIOClient);
        concurrentHashMap.put(userId, sessionIdClientCache);
    }

    /**
     * 根据用户ID获取所有通道信息
     *
     * @param userId
     * @return
     */
    public HashMap<UUID, SocketIOClient> getUserClient(String userId) {
        return concurrentHashMap.get(userId);
    }

    public List<String> getUsers() {
        List<String> list = new ArrayList<>();
        for (String key : concurrentHashMap.keySet()) {
            if (!concurrentHashMap.get(key).isEmpty()) {
                list.add(key);
            }
        }
        return list;
    }

    /**
     * 根据用户ID及页面sessionID删除页面链接信息
     *
     * @param userId
     * @param sessionId
     */
    public void deleteSessionClient(String userId, UUID sessionId) {
        concurrentHashMap.get(userId).remove(sessionId);
    }
}
 
