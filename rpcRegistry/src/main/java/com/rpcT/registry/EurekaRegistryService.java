package com.rpcT.registry;

import com.rpcT.rpcCore.ServiceMeta;

import java.io.IOException;


// TODO
public class EurekaRegistryService implements RegistryService{
    public EurekaRegistryService(String registryAddress) {
        // TODO
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        return null;
    }

    @Override
    public void destroy() throws IOException {

    }
}
