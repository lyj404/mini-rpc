package ch.cloudns.lyj.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Description 服务注册表通用接口
 * @Date 2024/6/9
 * @Author lyj
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     *
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
