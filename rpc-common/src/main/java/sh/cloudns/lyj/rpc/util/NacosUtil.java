package sh.cloudns.lyj.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description  管理nacos连接的工具类
 * 提供了连接 Nacos、注册服务、获取服务实例列表和注销服务的方法
 * @Date 2024/6/12
 * @Author lyj
 */
public class NacosUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosUtil.class);

    /**
     * Nacos 命名服务的静态实例
     */
    private static final NamingService NAMING_SERVICE;

    /**
     * 存储已注册的服务名称的集合
     */
    private static final Set<String> serviceNames = new HashSet<>();

    /**
     * 存储当前服务的地址信息
     */
    private static InetSocketAddress ADDRESS;

    /**
     * Nacos 服务器地址
     */
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        // 初始化 Nacos 命名服务实例
        NAMING_SERVICE = getNacosNamingService();
    }

    /**
     * 获取 Nacos 命名服务的实例。
     * @return NamingService Nacos 命名服务实例
     */
    public static NamingService getNacosNamingService(){
        try {
            // 使用 Nacos 提供的工厂方法创建并返回 NamingService 实例
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e){
            LOGGER.error("连接nacos时发生错误：", e);
            throw new RpcException(RpcErrorEnum.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 注册服务到 Nacos。
     * @param serviceName 服务名称
     * @param address 服务地址
     * @throws NacosException Nacos 异常
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException{
        // 注册服务实例到 Nacos，并更新本地存储的地址和服务名称信息
        NAMING_SERVICE.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.ADDRESS = address;
        serviceNames.add(serviceName);
    }

    /**
     * 获取所有服务实例。
     * @param serviceName 服务名称
     * @return List<Instance> 服务实例列表
     * @throws NacosException Nacos 异常
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        // 从 Nacos 获取并返回指定服务的所有实例
        return NAMING_SERVICE.getAllInstances(serviceName);
    }

    /**
     * 清除注册的服务。
     */
    public static void clearRegister(){
        // 如果已注册的服务名称不为空且地址不为空，则进行注销操作
        if (!serviceNames.isEmpty() && ADDRESS != null){
            String host = ADDRESS.getHostName();
            int port = ADDRESS.getPort();
            // 遍历服务名称集合，尝试注销每个服务
            for (String serviceName : serviceNames) {
                try {
                    NAMING_SERVICE.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    LOGGER.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}