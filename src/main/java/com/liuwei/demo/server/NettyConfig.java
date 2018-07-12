package com.liuwei.demo.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class NettyConfig {

    /** 客户端连接服务器相关配置 */
    @Value("${netty.socket.host}")
    private String serverSocketHost;
    @Value("${netty.socket.port}")
    private int serverSocketPort;
    @Value("${netty.keepalived.timeout}")
    private int keepalivedTimeout;
}
