package com.rpcT.rpcProtocols.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsgHeader implements Serializable {

    // 共18 byte

    private short magic; // 魔数，2byte

    private byte version; // 版本号，1byte

    private byte serialization; // 序列化算法，1byte

    private byte msgType; //报文类型,1byte

    private byte status; //消息状态,1byte

    private long requestId; //消息ID，8byte

    private int msgLen; // 数据长度,4byte
}
