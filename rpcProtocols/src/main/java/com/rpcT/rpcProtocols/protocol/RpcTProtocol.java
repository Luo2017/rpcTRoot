package com.rpcT.rpcProtocols.protocol;


import lombok.Data;

import java.io.Serializable;

@Data
public class RpcTProtocol<T> implements Serializable {
    private MsgHeader header;
    private T body;

}
