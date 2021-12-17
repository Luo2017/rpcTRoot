package com.rpct.rpcprovider.facade;

import com.rpcT.facade.HelloFacade;
import com.rpct.rpcprovider.annotation.RpcService;

// 主要作用是注入 @Component，注意版本号要和consumer @RpcReference 完全一致才行，这个版本号很重要
@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "1.0.0")
public class HelloFacadeImpl implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello version 1.0, this is " + name;
    }
}
