package com.liuwei.demo.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class InboundDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {

        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String strMessage = new String(bytes, Charset.forName("UTF-8"));
        out.add(strMessage);
    }
}
