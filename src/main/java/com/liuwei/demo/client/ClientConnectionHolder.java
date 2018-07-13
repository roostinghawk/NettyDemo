package com.liuwei.demo.client;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端连接的内存保持管理类。
 */
@Slf4j
public class ClientConnectionHolder {

    private ChannelHandlerContext channelHandlerContext;

    private static ClientConnectionHolder instance;

    private ClientConnectionHolder() {
    }

    public synchronized static ClientConnectionHolder getInstance() {
        if (ClientConnectionHolder.instance == null) {
            ClientConnectionHolder.instance = new ClientConnectionHolder();
        }
        return ClientConnectionHolder.instance;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }


    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }


}
