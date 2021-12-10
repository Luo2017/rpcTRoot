package com.rpcT.rpcProtocols.handler;

import com.rpcT.rpcCore.RpcFuture;
import com.rpcT.rpcCore.RpcRequestHolder;
import com.rpcT.rpcCore.RpcResponse;
import com.rpcT.rpcProtocols.protocol.MsgHeader;
import com.rpcT.rpcProtocols.protocol.RpcTProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// 经过 decoder 的可以是 request 也可以是 response
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcTProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcTProtocol<RpcResponse> rpcResponseRpcTProtocol) throws Exception {
        long requestId = rpcResponseRpcTProtocol.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(rpcResponseRpcTProtocol.getBody()); // 设置监听 promise 的结果
    }
}
