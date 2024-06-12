package sh.cloudns.lyj.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.factory.ThreadPoolFactory;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.util.concurrent.ExecutorService;

/**
 * @Date 2024/6/13
 * @Author lyj
 */
public class ShutdownHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return SHUTDOWN_HOOK;
    }

    public void addClearAllHook(){
        LOGGER.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegister();
            threadPool.shutdown();
        }));
    }
}
