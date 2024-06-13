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
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description 用于获取 Channel 对象
 * @Date 2024/6/10
 * @Author lyj
 */
public class ChannelProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();


    /**
     * 重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel;

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
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
        // 创建一个 CountDownLatch 计数器，用于等待连接完成
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try{
            // 尝试连接到服务器
            connect(bootstrap, inetSocketAddress, countDownLatch);
            // 等待连接完成或发生异常
            countDownLatch.await();
        } catch(InterruptedException e){
            LOGGER.error("获取channel时发生错误：", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch){
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch){
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("客户端连接成功");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if (retry == 0){
                LOGGER.error("客户端连接失败：重试次数已用完，放弃连接!");
                countDownLatch.countDown();
                throw new RpcException(RpcErrorEnum.CLIENT_CONNECT_SERVER_FAILURE);
            }
            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
            // 第 1 次重试 (order = 1): delay = 1 << 1 = 2 秒
            // 第 2 次重试 (order = 2): delay = 1 << 2 = 4 秒
            // 第 3 次重试 (order = 3): delay = 1 << 3 = 8 秒
            int delay = 1 << order;
            LOGGER.error("{}: 连接失败，第{}次重连...", new Date(), order);
            bootstrap.config().group().schedule(() -> connect(bootstrap,inetSocketAddress, retry -1, countDownLatch),
                    delay, TimeUnit.SECONDS);
        });
    }

    /**
     * 初始化BootStrap
     * @return 初始化完成的Bootstrap
     */
    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap1 = new Bootstrap();
        bootstrap1.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 设置连接超时的时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启TCP底层的心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap1;
    }
}
