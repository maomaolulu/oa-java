package com.ruoyi.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication(scanBasePackages = "com.ruoyi")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ruoyi")
//@Order(1)
public class ChatApplication {
//    private SocketIOServer server;
    public static void main(String[] args){
        SpringApplication.run(ChatApplication.class,args);
        System.out.println("socket.io通信启动成功！");
    }
//    @Bean
//    public SocketIOServer socketIOServer() {
//        Configuration config = new Configuration();
//        //Netty-Socketio服务器端口
//        config.setPort(8083);
//        this.server = new SocketIOServer(config);
//        return server;
//    }

//    @Bean
//    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
//        return new SpringAnnotationScanner(socketServer);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        server.start();
//        System.out.println("启动正常");
//    }
}

