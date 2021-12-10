package com.rpcT.rpcProtocols.codec;

import com.rpcT.rpcCore.RpcRequest;
import com.rpcT.rpcCore.RpcResponse;
import com.rpcT.rpcProtocols.protocol.MsgHeader;
import com.rpcT.rpcProtocols.protocol.MsgType;
import com.rpcT.rpcProtocols.protocol.ProtocolConstants;
import com.rpcT.rpcProtocols.protocol.RpcTProtocol;
import com.rpcT.rpcProtocols.serialization.RpcSerialization;
import com.rpcT.rpcProtocols.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

// 相当于 http codec 的 decoder，实际就是将 byte 转换成 Message(http 或者)
public class RpcTDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return; // 至少要比 header 的长度大于等于
        }
        in.markReaderIndex();
        short magic = in.readShort(); // readerIndex 会改变
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number 错误！ " + magic);
        }
        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte(); // request,response,heartbeat
        byte status = in.readByte();
        long requestId = in.readLong();
        int bodyLen = in.readInt();
        // 此时 readableBytes 是剩下的没有读的字节数
        if (in.readableBytes() < bodyLen) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[bodyLen];
        in.readBytes(data);
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        // 有可能 byteBuf in 的长度大于 bodyLen，这样 out.size() 改变了，decode 返回时剩下没读的仍然下次仍然可以被读
        // 我们的 decode 只解析一个，实际上可以无限解析，直到解析不出来为止再返回
        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(bodyLen);
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);
        switch (msgTypeEnum) {
            case  REQUEST:
                // 注意 class<?> 不是集合，就是一个类
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcTProtocol<RpcRequest> protocol = new RpcTProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    list.add(protocol);
                }
                break; // 最好加上 break
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcTProtocol<RpcResponse> protocol = new RpcTProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    list.add(protocol);
                }
            case HEARTBEAT:
                // TODO
                break;
        }
    }
}
