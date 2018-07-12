package com.liuwei.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author liuwei
 * @date 2018/7/12
 */
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
        private SocketClient client;

        public ClientInboundHandler(SocketClient client) {
            this.client = client;
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                client.createBootstrap(new Bootstrap(), eventLoop);
            }}, 1L, TimeUnit.SECONDS);
            super.channelInactive(ctx);
        }
}
