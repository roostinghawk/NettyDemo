package com.liuwei.demo.handler;

import com.liuwei.demo.server.ConnectionHolder;
import com.liuwei.demo.server.NettyConstants;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通道闲置超时处理（心跳处理）
 *
 * @author liuyg
 */
@Slf4j
@Component
@Sharable
public class ClientInactiveHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        try {
            String cbn = ctx.channel().attr(NettyConstants.ATTR_CBN).get();
            if(cbn != null) {
                ConnectionHolder.getInstance().releaseAndRemove(cbn);
            }

            System.out.println(String.format("连接[%s]已断开!", cbn));
        } finally {
            super.channelInactive(ctx);
        }
    }
}
