package com.rpct.rpcprovider.core;

import com.rpcT.registry.RegistryService;
import com.rpcT.rpcCore.RpcServiceHelper;
import com.rpcT.rpcCore.ServiceMeta;
import com.rpcT.rpcProtocols.codec.RpcTDecoder;
import com.rpcT.rpcProtocols.codec.RpcTEncoder;
import com.rpcT.rpcProtocols.handler.RpcRequestHandler;
import com.rpct.rpcprovider.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;

    private  int serverPort;

    private RegistryService registryService;

    private Map<String, Object> rpcServiceMap = new HashMap<>();

    public RpcProvider(int serverPort, RegistryService registryService) {
        this.serverPort = serverPort;
        this.registryService = registryService;
    }

    private void startRpcServer() throws Exception {
        this.serverAddress = InetAddress.getLocalHost().getHostAddress(); // 127.0.0.1
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcTEncoder())
                                    .addLast(new RpcTDecoder()) // encoder 和 decoder 一个是出站一个是入站，顺序可以调换
                                    .addLast(new RpcRequestHandler(rpcServiceMap));
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("服务器 {} started on port {}", this.serverAddress, this.serverPort);
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    // 在容器中所有 bean 的属性被初始化完成时即被调用
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("start rpc server error: " + e);
            }
        }).start();
    }

    // 任何注解了 @RpcService 的都会调用下面的方法
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);// bean 指的是容器中的所有bean
        if (rpcService != null) {
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();

            try {
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceName(serviceName);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(serverPort);

                registryService.register(serviceMeta);
                rpcServiceMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()), bean);
            } catch (Exception e) {
                log.error("注册服务失败！");
            }
        }
        return bean;
    }
}
