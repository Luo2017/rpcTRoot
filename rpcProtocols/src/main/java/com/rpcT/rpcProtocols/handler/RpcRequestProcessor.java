package com.rpcT.rpcProtocols.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcRequestProcessor {

    // 对于一个静态变量，如果创建一个实例后，就会一直存在的
    private static ThreadPoolExecutor threadPoolExecutor;

    public static void submitRequest(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcRequestProcessor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

}
