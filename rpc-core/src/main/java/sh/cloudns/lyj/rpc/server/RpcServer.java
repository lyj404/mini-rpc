package sh.cloudns.lyj.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Description 远程方法调用的提供者（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class RpcServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    private final ExecutorService threadPool;

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 5;

    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 50;

    /**
     * 线程的存活时间
     */
    private static final int KEEP_ALIVE_TIME = 60;

    /**
     * 阻塞队列，存储等待执行的任务
     */
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;


    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        // 阻塞队列，存储等待执行的任务
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);
        // 创建一个默认线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("RPC服务启动...");
            Socket socket;
            // 循环等待客户端连接
            while ((socket = serverSocket.accept()) != null){
                LOGGER.info("消费者连接：{}:{}", socket.getInetAddress(), socket.getPort());
                this.threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e){
            LOGGER.error("连接是发生错误：", e);
        }
    }
}
