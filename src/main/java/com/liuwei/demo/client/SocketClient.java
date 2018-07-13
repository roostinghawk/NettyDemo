package com.liuwei.demo.client;

import com.liuwei.demo.message.ClientMessage;
import com.liuwei.demo.server.NettyConstants;
import com.liuwei.demo.util.GsonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author liuwei
 * @date 2018/7/12
 */
public class SocketClient
{
    private EventLoopGroup loop = new NioEventLoopGroup();
    public static void main( String[] args )
    {
        new SocketClient().run();

        ClientConnectionHolder holder = ClientConnectionHolder.getInstance();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            try {
                System.out.print("Enter something : ");
                String input = br.readLine();
                ClientMessage clientMessage = new ClientMessage();
                clientMessage.setCommand(NettyConstants.COM_CHAT);
                clientMessage.setData(input);
                holder.getChannelHandlerContext().writeAndFlush(
                        Unpooled.copiedBuffer(GsonUtils.toJson(clientMessage), CharsetUtil.UTF_8));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) {
        if (bootstrap != null) {
            final ClientInboundHandler handler = new ClientInboundHandler(this);
            bootstrap.group(eventLoop);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(handler)
                    .addLast(new ClientMessageHandler())
                    .addLast(new ClientActiveHandler());
                }
            });
            bootstrap.remoteAddress("localhost", 1888);
            bootstrap.connect().addListener(new ConnectionListener(this));
        }
        return bootstrap;
    }
    public void run() {
        createBootstrap(new Bootstrap(), loop);
    }
}
