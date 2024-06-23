package ch.cloudns.lyj.rpc.transport.socket.server;

import ch.cloudns.lyj.rpc.factory.ThreadPoolFactory;
import ch.cloudns.lyj.rpc.handler.RequestHandler;
import ch.cloudns.lyj.rpc.provider.ServiceProviderImpl;
import ch.cloudns.lyj.rpc.registry.NacosServiceRegistry;
import ch.cloudns.lyj.rpc.serializer.CommonSerializer;
import ch.cloudns.lyj.rpc.transport.AbstractRpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Description Socket方式远程方法调用的提供者（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
@Slf4j
@Component
public class SocketServer extends AbstractRpcServer {

    @Autowired
    private ExecutorService threadPool;
    private final RequestHandler requestHandler = new RequestHandler();

    @Bean
    public ExecutorService serviceTask(){
        return ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    public void registerService() {
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        scanServices();
    }

    @Override
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("RPC服务启动...");
            Socket socket;
            // 循环等待客户端连接
            while ((socket = serverSocket.accept()) != null){
                log.info("消费者连接：{}:{}", socket.getInetAddress(), socket.getPort());
                // 使用线程池来处理RPC请求
                this.threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER)));
            }
            threadPool.shutdown();
        } catch (IOException e){
            log.error("连接是发生错误：", e);
        }
    }
}
