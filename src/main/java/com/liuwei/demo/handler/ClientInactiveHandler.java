package com.liuwei.demo.handler;

import com.liuwei.demo.server.ConnectionHolder;
import com.liuwei.demo.server.NettyConstants;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通道关闭超时处理（客户端关闭触发等情况）
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
