package sh.cloudns.lyj.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public RpcServer() {
        // 核心线程数
        int corePoolSize = 5;
        // 最大线程数
        int maximumPoolSize = 50;
        // 线程的存活时间
        long keepAliveTime = 60;
        // 阻塞队列，存储等待执行的任务
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<Runnable>(100);
        // 创建一个默认线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void register(Object service, int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("RPC服务正在启动");
            Socket socket;
            // 循环等待客户端连接
            while ((socket = serverSocket.accept()) != null){
                LOGGER.info("客户端连接成功！IP地址为：", socket.getInetAddress());
                this.threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e){
            LOGGER.error("连接是发生错误：", e);
        }
    }
}
