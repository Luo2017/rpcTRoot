package com.com.rpcT.rpcCore;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String serviceVersion; // 服务版本

    private String className; // 服务接口名

    private String methodName; // 服务方法名

    private Object[] params; // 方法参数列表

    private Class<?>[] parameterTypes; // 方法参数类型列表，Class 不是集合，表示的就是一个类

}
