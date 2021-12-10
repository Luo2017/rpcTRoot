package com.rpcT.rpcCore;

import io.netty.util.concurrent.Promise;
import lombok.Data;


@Data
public class RpcFuture<T> {

    private Promise<T> promise; // 自己有 addListener，setSuccess 等函数
    private long timeout;

    // 不用 RpcFuture<T> 因为这个<T> 是 promise 的
    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }

}
