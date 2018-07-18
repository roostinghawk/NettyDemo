package com.liuwei.demo.client;

import com.liuwei.demo.server.ConnectionHolder;
import com.liuwei.demo.util.GsonUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.liuwei.demo.server.NettyConstants.*;

@Slf4j
@Component
@Sharable
public class ClientMessageHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        log.info("接收到：" +message);
    }
}
