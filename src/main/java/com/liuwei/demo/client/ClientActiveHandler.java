package com.liuwei.demo.client;

/**
 * @author liuwei
 * @date 2018/6/28
 */
import com.liuwei.demo.message.ClientMessage;
import com.liuwei.demo.server.NettyConstants;
import com.liuwei.demo.util.GsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Random;


public class ClientActiveHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientConnectionHolder connectionHolder = ClientConnectionHolder.getInstance();
        connectionHolder.setChannelHandlerContext(ctx);

        // Tell server who am I
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setCommand(NettyConstants.COM_CONNECT);
        clientMessage.setData(String.valueOf(Math.random()));
        ctx.writeAndFlush(Unpooled.copiedBuffer(GsonUtils.toJson(clientMessage), CharsetUtil.UTF_8));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) throws Exception {
        System.out.println("客户端收到消息：" + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
