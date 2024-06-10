package sh.cloudns.lyj.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.RpcClient;
import sh.cloudns.lyj.rpc.codec.CommonDecoder;
import sh.cloudns.lyj.rpc.codec.CommonEncoder;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyClient implements RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap BOOTSTRAP;

    private CommonSerializer serializer;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    private String host;
    private int port;
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            LOGGER.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        BOOTSTRAP.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new NettyClientHandler());
            }
        });
        try {
            // 同步连接到服务器
            ChannelFuture future = BOOTSTRAP.connect(host, port).sync();
            LOGGER.info("客户端连接到服务器：{}:{}", host, port);
            // 获取 Netty 通道对象
            Channel channel = future.channel();
            if (channel != null){
                // 将请求写入通道，并刷新发送缓冲区
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()){
                        LOGGER.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                    } else {
                        LOGGER.error("发送消息是产生错误：", future1.cause());
                    }
                });
                // 等待通道关闭
                channel.closeFuture().sync();
                // 从通道属性中获取存储的 RpcResponse 对象
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                // 返回 RpcResponse 中的数据
                return rpcResponse.getData();
            }
        } catch (InterruptedException e){
            LOGGER.error("发送消息是产生错误：{}", e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
