package sh.cloudns.lyj.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.codec.CommonDecoder;
import sh.cloudns.lyj.rpc.codec.CommonEncoder;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取 Channel 对象的提供者。
 * 该类提供了获取 Netty Channel 的静态方法，用于客户端连接。
 * @Date 2024/6/10
 * @Author lyj
 */
public class ChannelProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProvider.class);

    /**
     * 用于处理网络事件的 EventLoopGroup
     */
    private static EventLoopGroup eventLoopGroup;

    /**
     * Netty Bootstrap 实例，用于客户端启动
     */
    private static final Bootstrap bootstrap = initializeBootstrap();

    /**
     * 用于存储 Channel 对象的线程安全 Map，以 InetSocketAddress 和序列化器类型作为键
     */
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();


    /**
     * 获取 Channel 对象的方法。
     * @param inetSocketAddress 服务端的地址和端口。
     * @param serializer 序列化器实例。
     * @return 返回 Channel 对象。
     */
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        // 创建一个键，用于在 channels Map 中唯一标识一个 Channel
        String key = inetSocketAddress.toString() + serializer.getCode();
        // 检查 channels Map 中是否已经存在对应的 Channel
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            // 如果 Channel 不为空且处于活跃状态，则返回该 Channel
            if (channels != null && channel.isActive()) {
                return channel;
            } else {
                // 如果 Channel 不存在或不再活跃，从 channels Map 中移除
                assert channels != null;
                channels.remove(key);
            }
        }
        // 设置 Bootstrap 的初始化器，添加自定义的编解码器和处理器
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                // 自定义编解码器 RpcResponse->ByteBuf
                channel.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel;
        try{
            // 尝试连接到服务器，并获取 Channel 对象
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("获取channel时发生错误：", e);
            return null;
        }
        // 将新创建的 Channel 添加到 channels Map 中
        channels.put(key, channel);
        return channel;
    }

    /**
     * 连接到服务器的方法，使用 CompletableFuture 来异步获取 Channel。
     * @param bootstrap Netty Bootstrap 实例。
     * @param inetSocketAddress 服务端的地址和端口。
     * @return 返回一个 Channel 对象。
     * @throws ExecutionException 执行异常。
     * @throws InterruptedException 中断异常。
     */
    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("客户端连接成功");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        // 等待直到连接完成并返回 Channel
        return completableFuture.get();
    }

    /**
     * 初始化BootStrap
     * @return 初始化完成的Bootstrap
     */
    private static Bootstrap initializeBootstrap() {
        // 创建 EventLoopGroup
        eventLoopGroup = new NioEventLoopGroup();
        // 创建 Bootstrap 实例
        Bootstrap bootstrap = new Bootstrap();
        // 设置 EventLoopGroup
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 设置连接超时的时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启TCP底层的心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
