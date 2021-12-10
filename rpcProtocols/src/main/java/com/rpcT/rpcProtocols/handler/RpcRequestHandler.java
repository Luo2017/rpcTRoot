package com.rpcT.rpcProtocols.handler;

import com.rpcT.rpcCore.RpcRequest;
import com.rpcT.rpcCore.RpcResponse;
import com.rpcT.rpcProtocols.protocol.MsgHeader;
import com.rpcT.rpcProtocols.protocol.MsgStatus;
import com.rpcT.rpcProtocols.protocol.MsgType;
import com.rpcT.rpcProtocols.protocol.RpcTProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
// 放在 RpcTDecoder 后对解码成 RpcTProtocol<RpcRequest> 类型的数据进行处理，注意 <RpcResponse> 类型的不会被识别
// 只有完全匹配这个 RpcProtocol<RpcRequest> 才会被识别，这说明 <> 实际是起到实际作用的
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcTProtocol<RpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcTProtocol<RpcRequest> rpcRequestRpcTProtocol) throws Exception {
        // 处理 rpc 调用耗时，即 handle 函数由于不会被包装成一个 task，所以可能会阻塞运行
        // 这就是在 netty 线程的基础上继续添加了额外的线程执行
        // 正常 channelRead() 函数是 netty 线程执行的，现在提交给另一个线程执行了，并且只是交给另一个线程就立即返回了
        // 从而线程不会在这个 channel 上耽搁太多时间，当然调用了 ctx.writeAndFlush() 之后又会交给 netty 线程执行
        RpcRequestProcessor.submitRequest(() -> {
            RpcTProtocol<RpcResponse> protocol = new RpcTProtocol<>();
            RpcResponse response = new RpcResponse();
            MsgHeader header = rpcRequestRpcTProtocol.getHeader();
            header.setMsgType((byte)MsgType.RESPONSE.getType()); // getType 返回的是 int 需要进行强转
            try {
                Object res = handle(rpcRequestRpcTProtocol.getBody());
                response.setData(res);
                header.setStatus((byte)MsgStatus.SUCCESS.getCode());

                // 不需要对版本,requestId,序列化方法进行修改
                // 没有对 bodyLen 进行修改，是在序列化时进行的设置，因为设置的是字节数
                protocol.setHeader(header);
                protocol.setBody(response);
            } catch (Throwable throwable) {
                // throwable 是 Exception 的父类
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("处理 request " + header.getRequestId() + " 出现错误 " + throwable);
                // 注意失败了也要返回反馈
                protocol.setHeader(header);
                protocol.setBody(response);
            }
            // 写到 RpcTEncoder 中序列化为 byte
            // 注意是 ctx.writeAndFlush，直接送到 pipeline 的上一个即 encoder 中
            channelHandlerContext.writeAndFlush(protocol);
        });
    }

    // 返回的是具体类型，动态绑定
    private Object handle(RpcRequest request) throws Throwable {
        // TODO
        return request;
    }
}
