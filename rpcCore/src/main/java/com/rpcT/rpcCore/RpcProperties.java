package com.rpcT.rpcCore;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties( prefix = "rpc") // 如果不用 Enable 的话，这个会一直报红
// 虽然 application.properties 是在rpc provider 下，但是仍然可以扫描到，因为被rpcProviderAutoConfiguration 类 import 了
public class RpcProperties {

    private int servicePort;

    private String registryAddress;

    private String registryType;
}
