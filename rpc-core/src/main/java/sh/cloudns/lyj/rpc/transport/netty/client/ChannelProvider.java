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
 * @Description 用于获取 Channel 对象
 * @Date 2024/6/10
 * @Author lyj
 */
public class ChannelProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static final Bootstrap bootstrap = initializeBootstrap();
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();


    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            } else {
                assert channels != null;
                channels.remove(key);
            }
        }
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
            // 尝试连接到服务器
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("获取channel时发生错误：", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

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
        return completableFuture.get();
    }

    /**
     * 初始化BootStrap
     * @return 初始化完成的Bootstrap
     */
    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
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
