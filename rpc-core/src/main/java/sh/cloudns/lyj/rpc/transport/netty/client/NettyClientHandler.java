package sh.cloudns.lyj.rpc.transport.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.factory.SingletonFactory;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;

/**
 * @Description 处理 RpcResponse 的 Netty 处理器
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    /**
     * channelRead0 方法在数据被读取时调用。
     * 该方法用于处理从服务端接收到的 RpcResponse。
     * @param ctx 通道处理器的上下文
     * @param msg 接收到的 RpcResponse 对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
        try {
            log.info(String.format("客户端接收到信息： %s", msg));
            // 使用 UnprocessedRequests 的实例完成与请求 ID 相关联的 future
            unprocessedRequests.complete(msg);
        } finally {
            // 释放 RpcResponse 对象所占用的资源
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * exceptionCaught 方法在发生异常时调用。
     * 该方法用于处理通道处理过程中发生的异常。
     * @param ctx 通道处理器的上下文
     * @param cause 发生的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理过程调用时有错误发生：{}", cause.getMessage());
        // 关闭发生异常的 ChannelHandlerContext，释放资源
        ctx.close();
    }

    /**
     * userEventTriggered 方法在触发用户自定义事件时调用。
     * 该方法用于处理检测到的空闲状态，例如，当写入操作空闲时发送心跳包。
     * @param ctx 通道处理器的上下文
     * @param evt 触发的事件对象
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE){
                // 如果检测到写入操作空闲，则发送心跳包
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                // 获取 Channel 对象
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                // 写入心跳请求，并在失败时关闭通道
                assert channel != null;
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
