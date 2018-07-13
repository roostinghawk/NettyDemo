package com.liuwei.demo.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@Sharable
public class OutboundEncoder extends MessageToByteEncoder<String> {

    /**
     * 消息编码
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final String message, final ByteBuf out)
            throws UnsupportedEncodingException {
        out.writeBytes(message.getBytes("UTF-8"));
    }
}
