package sh.cloudns.lyj.rpc.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @Description 创建 ThreadPool(线程池) 的工具类
 * @Date 2024/6/10
 * @Author lyj
 */
public class ThreadPoolFactory {
    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 100;

    /**
     * 线程的存活时间
     */
    private static final int KEEP_ALIVE_TIME = 1;

    /**
     * 阻塞队列，存储等待执行的任务
     */
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private ThreadPoolFactory(){}

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon){
        // 使用有界队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadPool(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MINUTES, workQueue, threadFactory);
    }

    /**
     * 创建TreadFactory
     * @param threadNamePrefix 线程名字的前缀
     * @param daemon 是否为Daemon Thread（守护线程）
     * @return TreadFactory
     */
    private static ThreadFactory createThreadPool(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
