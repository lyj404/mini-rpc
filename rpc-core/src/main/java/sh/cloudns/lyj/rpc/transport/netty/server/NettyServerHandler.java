package sh.cloudns.lyj.rpc.transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.factory.SingletonFactory;
import sh.cloudns.lyj.rpc.handler.RequestHandler;

/**
 * @Description 处理RpcRequest的handler
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    /**
     * 重写 channelRead0 方法，处理接收到的 RpcRequest 对象
     * @param ctx ChannelHandlerContext
     * @param msg RpcRequest
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
            try {
                // 如果是心跳包，记录日志并返回
                if (msg.getHeartBeat()){
                    LOGGER.info("收到客户端心跳包...");
                    return;
                }
                LOGGER.info("服务器接收到请求：{}", msg);
                // 调用请求处理器处理请求，并获取处理结果
                Object result = requestHandler.handle(msg);

                // 检查通道是否活跃且可写，如果是，则发送响应
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
                } else {
                    LOGGER.error("通道不可写");
                }
            } finally {
                // 释放 RpcRequest 对象所占用的资源
                ReferenceCountUtil.release(msg);
            }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("处理过程调用时有错误发生：{}", cause.getMessage());
        // 关闭发生异常的 ChannelHandlerContext，释放资源
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果事件是 IdleStateEvent，检查是否长时间未收到数据
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 如果长时间未收到心跳包，记录日志并关闭连接
                LOGGER.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
