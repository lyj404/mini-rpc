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
    private RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
            try {
                if (msg.getHeartBeat()){
                    LOGGER.info("收到客户端心跳包...");
                    return;
                }
                LOGGER.info("服务器接收到请求：{}", msg);
                Object result = requestHandler.handle(msg);
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
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                LOGGER.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
