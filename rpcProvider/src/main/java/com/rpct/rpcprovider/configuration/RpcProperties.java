package com.rpct.rpcprovider.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties( prefix = "rpc") // 如果不用 Enable 的话，这个会一直报红
public class RpcProperties {

    private int servicePort;

    private String registryAddr;

    private String registryType;
}
