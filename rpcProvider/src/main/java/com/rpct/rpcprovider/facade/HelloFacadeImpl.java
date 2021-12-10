package com.rpct.rpcprovider.facade;

import com.rpcT.facade.HelloFacade;
import com.rpct.rpcprovider.annotation.RpcService;

// 主要作用是注入 @Component
@RpcService(serviceVersion = "1.0.0")
public class HelloFacadeImpl implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello" + name;
    }
}
