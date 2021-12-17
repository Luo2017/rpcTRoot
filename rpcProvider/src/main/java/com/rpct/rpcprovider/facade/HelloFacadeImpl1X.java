package com.rpct.rpcprovider.facade;

import com.rpcT.facade.HelloFacade;
import com.rpct.rpcprovider.annotation.RpcService;

// 同样的名字，同样的版本，但是生成不一样的 serviceMeta，注册到同一个 name 结点下，使用哈希环来获得具体的 service
// 本例中不注册，因为实际上无法体现注册中心的负载均衡效果
//@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "1.0.0")
public class HelloFacadeImpl1X implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello version 1.0's alternative, this is " + name;
    }
}
