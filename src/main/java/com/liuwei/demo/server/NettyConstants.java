package com.liuwei.demo.server;

import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

public class NettyConstants {

    private NettyConstants() {
    }

    public static final Charset defaultCharset = Charset.forName("utf-8");

    public static final AttributeKey<String> ATTR_CBN = AttributeKey.newInstance("CBN");

    /**
     * 错误信息：设备不在线
     */
    public static final String MSG_ERROR_OFFLINE = "设备不在线";

    /**
     * 错误信息：设备失联（长连接不存在）
     */
    public static final String MSG_ERROR_LOST_CONNECTIOn = "设备失联";

    public static String KEY_COMMAND = "command";

    public static String KEY_DATA = "data";

    public final static int COM_CONNECT = 0x20;
    public final static int COM_CHAT = 0x21;
}
