package sh.cloudns.lyj.rpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description 创建 ThreadPool(线程池) 的工具类
 * @Date 2024/6/10
 * @Author lyj
 */
public class ThreadPoolFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolFactory.class);

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

    private static Map<String, ExecutorService> threadPollsMap = new ConcurrentHashMap<>();

    private ThreadPoolFactory(){}

    /**
     * 创建默认的线程池，非守护线程。
     * @param threadNamePrefix 线程名前缀
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    /**
     * 创建默认的线程池。
     * @param threadNamePrefix 线程名前缀
     * @param daemon 是否为守护线程
     * @return ExecutorService 线程池实例
     */
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon){
        // 使用 computeIfAbsent 方法来创建或获取线程池实例
        // computeIfAbsent() 方法对 hashMap 中指定 key 的值进行重新计算，如果不存在这个 key，则添加到 hashMap 中
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix, key -> createThreadPool(threadNamePrefix, daemon));
        // 检查线程池是否已关闭或终止，如果是，则重新创建并更新到 map 中
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPollsMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPollsMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    /**
     * 关闭所有线程池。
     */
    public static void shutDownAll(){
        LOGGER.info("关闭所有线程池");
        // 使用 parallelStream 并行关闭线程池
        threadPollsMap.entrySet().parallelStream().forEach(entry -> {
            // 获取线程池实例
            ExecutorService executorService = entry.getValue();
            // 启动关闭线程池流程
            executorService.shutdown();
            LOGGER.info("关闭线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                // 等待线程池关闭
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e){
                LOGGER.error("关闭线程池失败！");
                // 中断时尝试立即关闭线程池
                executorService.shutdownNow();
            }
        });
    }

    /**
     * 创建线程池。
     * @param threadNamePrefix 线程名前缀
     * @param daemon 是否为守护线程
     * @return ExecutorService 线程池实例
     */
    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        // 使用有界队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        // 创建线程工厂
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        // 创建并返回线程池
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MINUTES, workQueue, threadFactory);
    }

    /**
     * 创建TreadFactory
     * @param threadNamePrefix 线程名字的前缀
     * @param daemon 是否为Daemon Thread（守护线程）
     * @return TreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                // 设置线程名前缀和守护线程属性
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                // 只设置线程名前缀
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        // 如果 threadNamePrefix 为 null，则使用默认线程工厂
        return Executors.defaultThreadFactory();
    }
}
