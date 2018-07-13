package com.liuwei.demo.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Hashtable;
import java.util.Map;

/**
 * 客户端连接的内存保持管理类。
 */
@Slf4j
public class ConnectionHolder {

    /**
     * 通过客户端SN、保存连接信息
     */
    private final Map<String, ChannelHandlerContext> onlineClientConnectionMap = new Hashtable<>();

    private static ConnectionHolder instance;

    private ConnectionHolder() {
    }

    public synchronized static ConnectionHolder getInstance() {
        if (ConnectionHolder.instance == null) {
            ConnectionHolder.instance = new ConnectionHolder();
        }
        return ConnectionHolder.instance;
    }

    /**
     * 保存连接
     *
     * @param sn
     * @param ctx
     */
    public synchronized void addIfNotExisted(
            final String sn,
            final ChannelHandlerContext ctx) {
        Assert.notNull(sn, "sn cannot be null");
        Assert.notNull(ctx, "connection cannot be null");

        if(!this.onlineClientConnectionMap.containsKey(sn)) {
            this.onlineClientConnectionMap.put(sn, ctx);
        }
    }

    public ChannelHandlerContext get(final String sn) {
        return this.onlineClientConnectionMap.get(sn);
    }

    public synchronized void releaseAndRemove(final String sn) {
        this.onlineClientConnectionMap.remove(sn);
    }

    public Map<String, ChannelHandlerContext> getOnlineClientConnectionMap() {
        return onlineClientConnectionMap;
    }

}
