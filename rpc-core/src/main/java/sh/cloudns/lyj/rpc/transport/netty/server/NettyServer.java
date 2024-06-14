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

    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
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
                            channel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                                    .addLast(new CommonEncoder(serializer))
                                                    .addLast(new CommonDecoder())
                                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            logger.error("启动服务器发生意外：", e);
        } finally {
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
        }
    }
}
