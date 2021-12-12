package com.rpct.rpcconsumer.core;

import com.rpcT.registry.RegistryService;
import com.rpcT.rpcCore.RpcRequest;
import com.rpcT.rpcCore.RpcServiceHelper;
import com.rpcT.rpcCore.ServiceMeta;
import com.rpcT.rpcProtocols.codec.RpcTDecoder;
import com.rpcT.rpcProtocols.codec.RpcTEncoder;
import com.rpcT.rpcProtocols.handler.RpcResponseHandler;
import com.rpcT.rpcProtocols.protocol.RpcTProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcTEncoder())
                                .addLast(new RpcTDecoder())
                                .addLast(new RpcResponseHandler());
                        // 客户端只需发送 request，所要处理的只有 response
                    }
                });

    }

    public void sendRequest(RpcTProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        Object[] params = request.getParams();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        int invokerHashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode(); // 以 name#version.hashCode() 的形式表示
        ServiceMeta serviceMetaData = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMetaData != null) {
            // 虽然 pom.xml 中没有，但是从依赖的其它子module可以获得 netty 包并使用
            // 这个 sync() 实际是不需要的
            ChannelFuture future = bootstrap.connect(serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort()).sync();
            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    log.info("成功连接到 server {}  port: {}", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                } else {
                    log.error("连接 server {}  port: {} 失败", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                }
            });
            future.channel().writeAndFlush(protocol);
        }
    }
}
