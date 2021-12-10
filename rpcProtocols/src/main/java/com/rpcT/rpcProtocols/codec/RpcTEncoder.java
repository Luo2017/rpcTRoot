package com.rpcT.rpcProtocols.codec;

import com.rpcT.rpcProtocols.protocol.MsgHeader;
import com.rpcT.rpcProtocols.protocol.RpcTProtocol;
import com.rpcT.rpcProtocols.serialization.RpcSerialization;
import com.rpcT.rpcProtocols.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

// 注意 RpcTProtocol 是输入类型，Object 是 body 的类型，可以是 RpcRequest 或者 RpcResponse
public class RpcTEncoder extends MessageToByteEncoder<RpcTProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcTProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        MsgHeader header = msg.getHeader(); // @Data
        // 下面必须是按照顺序的
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization()); // 0x10 表示 HESSIAN，0x20 则用 JSON 序列化
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        // msgLen 是 body 的长度，不包括 header
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        byte[] data = rpcSerialization.serialize(msg.getBody()); // 序列化的结果是 byte[]，反序列化的输入也是 byte[]
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
