package com.rpcT.rpcCore;

public class RpcServiceHelper {

    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("#", serviceName, serviceVersion);
        // 返回的字符串是 serviceName#serviceVersion 这种表示
    }
}
