package sh.cloudns.lyj.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Description 服务发现接口
 * @Date 2024/6/12
 * @Author lyj
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体
     * @param serviceName  服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
