package com.liuwei.demo.server;

import com.liuwei.demo.codec.InboundDecoder;
import com.liuwei.demo.codec.OutboundEncoder;
import com.liuwei.demo.handler.ChannelIdleTimoutHandler;
import com.liuwei.demo.handler.ClientInactiveHandler;
import com.liuwei.demo.handler.MessageHandler;
import com.liuwei.demo.handler.ServerExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Slf4j
@Configuration
@Component
public class SocketServer {

    @Autowired
    private ChannelIdleTimoutHandler channelIdleTimoutHandler;
    @Autowired
    private ClientInactiveHandler clientInactiveHandler;
    @Autowired
    private ServerExceptionHandler serverExceptionHandler;
    @Autowired
    private NettyConfig nettyConfig;
    @Autowired
    private MessageHandler messageHandler;

    private final LoggingHandler loggingHandler = new LoggingHandler();

    // 接受和处理新连接
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    // 工作线程
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public ServerBootstrap startServer() {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(final SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("loggingHandler", SocketServer.this.loggingHandler);
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(
                                SocketServer.this.nettyConfig.getKeepalivedTimeout(),
                                SocketServer.this.nettyConfig.getKeepalivedTimeout(),
                                SocketServer.this.nettyConfig.getKeepalivedTimeout()));
                        pipeline.addLast("idleEventHandler", SocketServer.this.channelIdleTimoutHandler);
                        pipeline.addLast("decoder", new InboundDecoder());
                        pipeline.addLast("encoder", new OutboundEncoder());
                        pipeline.addLast("messageHandler", SocketServer.this.messageHandler);
                        pipeline.addLast("serverExceptionHandler", SocketServer.this.serverExceptionHandler);
                        pipeline.addLast("inactiveHandler", SocketServer.this.clientInactiveHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            final ChannelFuture channelFuture =
                    serverBootstrap.bind(this.nettyConfig.getServerSocketHost(), this.nettyConfig.getServerSocketPort());
            channelFuture.addListener(
                    future -> System.out.println("Listening：" + SocketServer.this.nettyConfig.getServerSocketHost() + ":"
                            + SocketServer.this.nettyConfig.getServerSocketPort()));
            channelFuture.sync();
        } catch (final InterruptedException ex) {
            SocketServer.log.error("server启动出现异常", ex);
        }
        System.out.println("！！！SocketServer 成功启动！！！");
        return serverBootstrap;
    }

    public void stopServer() {
        this.bossGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("！！！SocketServer服务组件已成功关闭！！！");
            } else {
                System.out.println("！！！SocketServer服务组件关闭失败！！！");
            }
        });
        this.workerGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("！！！SocketServer工作组件已成功关闭！！！");
            } else {
                System.out.println("！！！SocketServer工作组件关闭失败！！！");
            }
        });
    }

}
