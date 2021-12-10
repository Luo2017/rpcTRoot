package com.rpcT.rpcCore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestHolder {
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    // 记录 response 的
    public final static Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();

}
