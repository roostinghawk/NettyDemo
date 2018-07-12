package com.liuwei.demo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class Bootstrap implements ApplicationListener<Bootstrap.StartServerEvent> {

    @Autowired
    private SocketServer socketServer;

    @Override
    public void onApplicationEvent(final StartServerEvent event) {
        this.startServer();
    }

    /**
     * 按顺序启动服务<br>
     * <li>数据存储的flume客户端
     * <li>接收终端状态变更的flume客户端
     * <li>SocketServer服务
     * <li>接收业务下发指令的flume服务端
     */
    private void startServer() {
        this.socketServer.startServer();
    }

    /**
     * 按顺序关闭服务<br>
     * <li>接收业务下发指令的flume服务端
     * <li>SocketServer服务
     * <li>接收终端状态变更的flume客户端
     * <li>数据存储的flume客户端
     */
    @PreDestroy
    public void stopServer() {
        this.socketServer.stopServer();
    }

    public static class StartServerEvent extends ApplicationEvent {
        private static final long serialVersionUID = 1L;

        public StartServerEvent(final Object source) {
            super(source);
        }
    }
}
