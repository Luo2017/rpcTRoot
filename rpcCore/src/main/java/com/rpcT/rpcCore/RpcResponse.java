package com.rpcT.rpcCore;


import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private Object data; // 请求结果

    private String message; // 错误信息
}
