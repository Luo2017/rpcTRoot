package com.rpct.rpcprovider.configuration;


import com.rpcT.registry.RegistryFactory;
import com.rpcT.registry.RegistryService;
import com.rpcT.registry.RegistryType;
import com.rpcT.rpcCore.RpcProperties;
import com.rpct.rpcprovider.core.RpcProvider;
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

    @Bean
    public RpcProvider init() throws Exception {
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService registryService = RegistryFactory.getInstance(rpcProperties.getRegistryAddress(),type);
        return new RpcProvider(rpcProperties.getServicePort(), registryService);
    }

}
