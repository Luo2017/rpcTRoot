package com.rpct.rpcprovider.configuration;


import com.rpcT.rpcCore.RpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {


    // 这个不是注入，而是在容器中找注入的
    @Resource
    private RpcProperties rpcProperties;


}
