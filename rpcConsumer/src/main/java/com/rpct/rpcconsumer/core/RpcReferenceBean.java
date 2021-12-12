package com.rpct.rpcconsumer.core;


import com.rpcT.registry.RegistryFactory;
import com.rpcT.registry.RegistryService;
import com.rpcT.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;

// 不用 @Data
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;

    private String registryAddress; // 包含了 port

    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {
        // 注意 valueOf() 函数的使用，可以让我们将 String 类型的 registryType 转换成 enum 类型的，不是自定义的
        // registryAddress 包含了端口
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddress, RegistryType.valueOf(registryType));
        // 实例化代理类，负责调用服务
        this.object = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new RpcInvokerProxy(serviceVersion, timeout, registryService));
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
