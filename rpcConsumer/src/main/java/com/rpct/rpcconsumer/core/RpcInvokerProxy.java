package com.rpct.rpcconsumer.core;

import com.rpcT.registry.RegistryService;
import com.rpcT.rpcCore.RpcFuture;
import com.rpcT.rpcCore.RpcRequest;
import com.rpcT.rpcCore.RpcRequestHolder;
import com.rpcT.rpcCore.RpcResponse;
import com.rpcT.rpcProtocols.protocol.*;
import com.rpcT.rpcProtocols.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RpcInvokerProxy implements InvocationHandler {

    private final String serviceVersion;

    private final long timeout;

    private final RegistryService registryService;

    public RpcInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcTProtocol<RpcRequest> protocol = new RpcTProtocol<>();
        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet(); // 新生成一个 requestId
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerialization((byte)SerializationTypeEnum.HESSIAN.getType()); // 服务端总是以 HESSIAN 序列化协议发送
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1); // 1 实际表示 fail
        protocol.setHeader(header);
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args); // invoke 的参数 args 就是 method 的 args
        protocol.setBody(request);
        RpcConsumer rpcConsumer = new RpcConsumer();
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        // 发起 RPC 远程调用
        rpcConsumer.sendRequest(protocol, this.registryService);
        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
