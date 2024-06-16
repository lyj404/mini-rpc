package sh.cloudns.lyj.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.factory.ThreadPoolFactory;
import sh.cloudns.lyj.rpc.util.NacosUtil;

/**
 * @Description 关闭钩子类，用于在应用程序关闭时执行清理操作
 * @Date 2024/6/13
 * @Author lyj
 */
public class ShutdownHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    /**
     * 静态单例，用于延迟初始化
     */
    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    /**
     * 获取关闭钩子的静态方法
     * @return 关闭钩子类
     */
    public static ShutdownHook getShutdownHook(){
        return SHUTDOWN_HOOK;
    }

    /**
     * 添加清理所有资源的钩子方法
     */
    public void addClearAllHook(){
        LOGGER.info("关闭后将自动注销所有服务");
        // 添加 JVM 关闭钩子，当 JVM 关闭时会执行传入的线程任务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 调用 NacosUtil 的 clearRegister 方法，用于注销所有注册到 Nacos 的服务
            NacosUtil.clearRegister();
            // 调用 ThreadPoolFactory 的 shutDownAll 方法，用于关闭所有创建的线程池
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
