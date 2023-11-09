//package com.ruoyi.socket.channel;
//
//import com.corundumstudio.socketio.Configuration;
//import com.corundumstudio.socketio.SocketConfig;
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//@Component
//public class PushServer implements InitializingBean {
//    @Resource
//    private MessageEventHandler messageEventHandler;
//
////    @Value("${push.server.port}")
////    private int serverPort;
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Configuration config = new Configuration();
//        config.setPort(8083);
//
//        SocketConfig socketConfig = new SocketConfig();
//        socketConfig.setReuseAddress(true);
//        socketConfig.setTcpNoDelay(true);
//        socketConfig.setSoLinger(0);
//        config.setSocketConfig(socketConfig);
//        config.setHostname("localhost");
//
//        SocketIOServer server = new SocketIOServer(config);
//        server.addListeners(messageEventHandler);
//        server.start();
//        System.out.println("启动正常");
//    }
//}