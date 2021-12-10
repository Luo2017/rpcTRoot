package com.rpcT.rpcProtocols.serialization;

// 这个没有写在 pom.xml 中，但是可以引入，可能还是从 parent 获得的，只不过没有写
import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component

public class HessianSerialization implements RpcSerialization{

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            throw new NullPointerException();
        }
        byte[] results;
        HessianSerializerOutput hessianOutput;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(obj);
            hessianOutput.flush();
            results = os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        // 返回的就是按照 hessian 协议格式的序列化结果
        return results;
    }

    // 加上这句话可以防止 （T） 那句报出黄线警告
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        if (data == null) {
            throw new NullPointerException();
        }
        T result;
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            HessianSerializerInput hessianSerializerInput = new HessianSerializerInput(is);
            result = (T) hessianSerializerInput.readObject(clz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        // 我们序列化输入是T obj 的结果是 byte[] ，而反序列化的输入也是 byte[]，输出是 T result
        return result;
    }
}
