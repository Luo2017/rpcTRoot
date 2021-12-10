package com.rpcT.rpcProtocols.serialization;

import java.io.IOException;

public interface RpcSerialization {

    // 不带 <T> 会报错，可能就是要事先声明这个 <T> 也可以在 RpcSerialization<T> 这种形式
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

}
