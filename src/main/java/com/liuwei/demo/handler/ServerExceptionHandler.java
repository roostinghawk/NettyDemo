package com.liuwei.demo.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Sharable
@Component
public class ServerExceptionHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {
        ServerExceptionHandler.log.error("捕获到异常信息，关闭连接", cause);
        ctx.close();
    }
}
