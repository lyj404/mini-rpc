package sh.cloudns.lyj.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.factory.SingletonFactory;
import sh.cloudns.lyj.rpc.loadbalancer.LoadBalancer;
import sh.cloudns.lyj.rpc.loadbalancer.RandomLoadBalancer;
import sh.cloudns.lyj.rpc.registry.NacosServiceDiscovery;
import sh.cloudns.lyj.rpc.registry.ServiceDiscovery;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyClient implements RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private static final Bootstrap BOOTSTRAP;
    private static final EventLoopGroup GROUP;

    static {
        GROUP = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(GROUP)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient(){
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse<?>> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            LOGGER.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            assert channel != null;
            if (!channel.isActive()) {
                GROUP.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            // 将请求写入通道，并刷新发送缓冲区
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()){
                    LOGGER.info(String.format("客户端发送消息：%s", rpcRequest));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    LOGGER.error("发送消息是产生错误：", future1.cause());
                }
            });
            // 等待通道关闭
            channel.closeFuture().sync();
        } catch (InterruptedException e){
            unprocessedRequests.remove(rpcRequest.getRequestId());
            LOGGER.error("发送消息是产生错误：", e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
