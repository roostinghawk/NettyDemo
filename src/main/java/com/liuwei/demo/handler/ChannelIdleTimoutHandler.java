package com.liuwei.demo.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Sharable
@Component
public class ChannelIdleTimoutHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            final IdleStateEvent e = (IdleStateEvent) evt;
            // 客户端需要定时上报，一旦超过一定时间未上报，就断开连接
            if (e.state() == IdleState.READER_IDLE) {
                ChannelIdleTimoutHandler.log.error("ChannelIdleTimoutHandler READER_IDLE Client: {}" + ctx.channel());
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.flush();
            }
        } else {
            // 继续传播事件
            ctx.fireUserEventTriggered(evt);
        }
    }
}
