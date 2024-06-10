package sh.cloudns.lyj.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.RequestHandler;
import sh.cloudns.lyj.rpc.RpcServer;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.util.ThreadPoolFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Description Socket方式远程方法调用的提供者（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketServer implements RpcServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    private final ExecutorService threadPool;

    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();


    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public void start(int port){
        if (serializer == null) {
            LOGGER.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("RPC服务启动...");
            Socket socket;
            // 循环等待客户端连接
            while ((socket = serverSocket.accept()) != null){
                LOGGER.info("消费者连接：{}:{}", socket.getInetAddress(), socket.getPort());
                this.threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e){
            LOGGER.error("连接是发生错误：", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
