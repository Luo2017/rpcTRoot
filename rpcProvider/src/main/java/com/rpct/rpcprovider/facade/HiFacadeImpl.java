package com.rpct.rpcprovider.facade;

import com.rpcT.facade.HiFacade;
import com.rpct.rpcprovider.annotation.RpcService;


@RpcService(serviceInterface = HiFacade.class, serviceVersion = "1.0.0")
public class HiFacadeImpl implements HiFacade {
    @Override
    public String hi(String name) {
        return "hi version 1.0, this is " + name;
    }
}
