package com.rpct.rpcprovider.facade;

import com.rpcT.facade.HelloFacade;
import com.rpct.rpcprovider.annotation.RpcService;

// 同样的名字注册到同一个结点下
@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "2.0.0")
public class HelloFacadeImpl2 implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello version 2.0, this is " + name;
    }
}
