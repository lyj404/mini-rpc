package sh.cloudns.lyj.rpc.transport.socket.server;

import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.factory.ThreadPoolFactory;
import sh.cloudns.lyj.rpc.handler.RequestHandler;
import sh.cloudns.lyj.rpc.provider.ServiceProviderImpl;
import sh.cloudns.lyj.rpc.registry.NacosServiceRegistry;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Description Socket方式远程方法调用的提供者（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port,DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void start(){
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("RPC服务启动...");
            Socket socket;
            // 循环等待客户端连接
            while ((socket = serverSocket.accept()) != null){
                logger.info("消费者连接：{}:{}", socket.getInetAddress(), socket.getPort());
                // 使用线程池来处理RPC请求
                this.threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e){
            logger.error("连接是发生错误：", e);
        }
    }
}
