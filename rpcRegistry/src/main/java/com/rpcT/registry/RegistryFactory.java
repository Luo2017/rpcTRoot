package com.rpcT.registry;

public class RegistryFactory {

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddress, RegistryType type) throws Exception {
        if (null == registryService) {
            // .class 只有一个
            synchronized (RegistryFactory.class) {
                if (null == registryService) {
                    switch (type) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryService(registryAddress);
                            break;
                        case EUREKA:
                            registryService = new EurekaRegistryService(registryAddress);
                            break;
                    }
                }
            }
        }
        return registryService;
    }

}
