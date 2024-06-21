package sh.cloudns.lyj.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.factory.SingletonFactory;
import sh.cloudns.lyj.rpc.loadbalancer.LoadBalancer;
import sh.cloudns.lyj.rpc.loadbalancer.impl.RandomLoadBalancer;
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
@Slf4j
public class NettyClient implements RpcClient {

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

    /**
     * 用于发送 Rpc 请求并返回响应的 CompletableFuture
     * @param rpcRequest 请求实体类
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<RpcResponse<?>> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        // 创建一个 CompletableFuture，用于异步处理 Rpc 响应
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        try {
            // 使用服务发现查询服务实例的地址信息
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
            // 获取 Channel 对象，用于发送 Rpc 请求
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            assert channel != null;
            if (!channel.isActive()) {
                GROUP.shutdownGracefully();
                return null;
            }
            // 将请求 ID 和 CompletableFuture 添加到未处理请求的管理器中
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            // 将请求写入通道，并刷新发送缓冲区
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()){
                    log.info(String.format("客户端发送消息：%s", rpcRequest));
                } else {
                    // 如果发送失败，关闭 Channel 并完成异常
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.error("发送消息是产生错误：", future1.cause());
                }
            });
        } catch (Exception e){
            // 如果发生异常，从未处理请求的管理器中移除请求 ID
            unprocessedRequests.remove(rpcRequest.getRequestId());
            log.error("发送消息是产生错误：", e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
