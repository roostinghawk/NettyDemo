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

    /**
     * 父线程组：接受和处理新连接
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    /**
     * 子线程组：用于IO操作
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public ServerBootstrap startServer() {
        // 服务端的启动类（Netty客户端使用Bootstrap来启动）
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 绑定处理IO操作的多线程事件循环群组
        serverBootstrap.group(this.bossGroup, this.workerGroup)
                // 绑定Channel（客户端使用NioSocketChannel）
                .channel(NioServerSocketChannel.class)
                // Handler初始化（每一个Channel建立时都会执行）-用匿名类ChannelInitializer实现
                // SocketChannel: 基于TCP/IP的双工Channel
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(final SocketChannel ch) throws Exception {
                        // Pipeline建立（所有事件处理都附着与Pipeline上）
                        final ChannelPipeline pipeline = ch.pipeline();
                        // 依次追加Handler（处理IO操作）
                        // Handler分为两类（ChannelInboundHandler和ChannelOutboundHandler），前者按照注册顺序执行，后者逆序执行
                        // LoggingHandler用于打印日志，默认Info级别（如果需要改变级别，可以在使用构造参数(LogLever.WARN)
                        pipeline.addLast("loggingHandler", SocketServer.this.loggingHandler);
                        // IdleStateHandler用于判定客户端连接状态（参数依次为读，写，读写超时时间），
                        // 一旦超过这个时间就将管道置为IDLE状态，可以在另一个handle中主动关闭连接（这里是下面的ChannelIdleTimoutHandler）
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(
                                SocketServer.this.nettyConfig.getKeepalivedTimeout(),
                                SocketServer.this.nettyConfig.getKeepalivedTimeout(),
                                SocketServer.this.nettyConfig.getKeepalivedTimeout()));
                        // 连接闲置（IDLE）时的处理器
                        pipeline.addLast("idleEventHandler", SocketServer.this.channelIdleTimoutHandler);
                        // 读取数据时的Decoder
                        pipeline.addLast("decoder", new InboundDecoder());
                        // 写数据时的Encoder
                        pipeline.addLast("encoder", new OutboundEncoder());
                        // 读取数据时的处理器
                        pipeline.addLast("messageHandler", SocketServer.this.messageHandler);
                        // 异常发生后的处理器
                        pipeline.addLast("serverExceptionHandler", SocketServer.this.serverExceptionHandler);
                        // 当Channel的Inactive被激发时的处理器（比如客户端主动关闭Channel）
                        pipeline.addLast("inactiveHandler", SocketServer.this.clientInactiveHandler);
                    }
                })
                // 队列中的请求数量
                .option(ChannelOption.SO_BACKLOG, 128)
                // 允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                // 禁止Nagle算法（适用于小数据即时传输，如果要传输大的数据量，要使用TCP_CORK）
                .option(ChannelOption.TCP_NODELAY, true)
                // 用于TCP连接：当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            // 添加ChannelFuture，用于异步事件发生后的操作
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
        // 优雅关闭线程组
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
