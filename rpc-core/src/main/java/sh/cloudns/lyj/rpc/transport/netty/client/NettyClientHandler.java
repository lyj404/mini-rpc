package sh.cloudns.lyj.rpc.transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcResponse;

/**
 * @Description 处理RpcResponse的handler
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            LOGGER.info(String.format("客户端接收到信息： %s", msg));
            // 创建一个 AttributeKey，用于在 Channel 中存储 RpcResponse 对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + msg.getRequestId());
            // 使用 AttributeKey 将 RpcResponse 对象存储到 Channel 的属性中
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("处理过程调用时有错误发生：{}", cause.getMessage());
        cause.printStackTrace();
        // 关闭发生异常的 ChannelHandlerContext，释放资源
        ctx.close();
    }
}
