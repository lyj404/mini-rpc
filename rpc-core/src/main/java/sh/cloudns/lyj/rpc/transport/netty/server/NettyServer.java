package sh.cloudns.lyj.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import sh.cloudns.lyj.rpc.codec.CommonDecoder;
import sh.cloudns.lyj.rpc.codec.CommonEncoder;
import sh.cloudns.lyj.rpc.hook.ShutdownHook;
import sh.cloudns.lyj.rpc.provider.ServiceProviderImpl;
import sh.cloudns.lyj.rpc.registry.NacosServiceRegistry;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.AbstractRpcServer;

import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyServer extends AbstractRpcServer {
    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer){
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    /**
     * 重写 AbstractRpcServer 的 start 方法，启动服务器
     */
    @Override
    public void start() {
        // 添加关闭钩子
        ShutdownHook.getShutdownHook().addClearAllHook();
        // 创建 bossGroup 和 workerGroup，用于接收连接和处理网络事件
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 配置服务器启动参数
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 添加日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置服务器端TCP连接的最大排队连接数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 设置 TCP 保活机制
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 用于禁用 Nagle 算法，数据包将立即发送而不是等待
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            // 添加空闲状态处理器
                            channel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                                    // 添加序列化处理器
                                                    .addLast(new CommonEncoder(serializer))
                                                    // 添加反序列化处理器
                                                    .addLast(new CommonDecoder())
                                                    // 添加 Netty 服务器业务处理器
                                                    .addLast(new NettyServerHandler());
                        }
                    });
            // 绑定主机和端口，并同步等待直到绑定完成
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            // 等待直到服务器 socket 关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            logger.error("启动服务器发生意外：", e);
        } finally {
            // 优雅关闭 bossGroup 和 workerGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
