package com.liuwei.demo.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuwei
 * @date 2018/7/13
 */
@Getter
@Setter
public class ClientMessage {

    private int command;

    private String data;
}
