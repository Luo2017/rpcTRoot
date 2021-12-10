package com.rpcT.rpcCore;

import lombok.Data;

@Data
public class ServiceMeta {

    // 和 RpcProperties 是有区别的
    private String serviceName;

    private String serviceVersion;

    private String ServiceAddr;

    private int servicePort;
}
