package com.liuwei.demo.handler;

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
public class MessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) {
        log.info("接收到：" + message);

        Map<String, Object> gsonMap = GsonUtils.GsonToMap(message.toString());
        Integer command = GsonUtils.Double2Integer((Double) gsonMap.get(KEY_COMMAND));
        String data = gsonMap.get(KEY_DATA).toString();
        ConnectionHolder connectionHolder = ConnectionHolder.getInstance();

        switch (command){
            // 第一次连接
            case COM_CONNECT:{
                connectionHolder.addIfNotExisted(data, ctx);
                break;
            }
            case COM_CHAT: {
                // 广播给各个客户端
                for(ChannelHandlerContext context : connectionHolder.getOnlineClientConnectionMap().values()){
                    if(context != ctx) {
                        context.writeAndFlush(message);
                    }
                }
                break;
            }
            default: {
                System.out.println("错误的命令类型！");
            }
        }

    }
}
