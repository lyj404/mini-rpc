package sh.cloudns.lyj.rpc.registry;

/**
 * @Description 服务注册表通用接口
 * @Date 2024/6/9
 * @Author lyj
 */
public interface ServiceRegistry {
    /**
     * 将服务注册到注册表
     * @param service 需要注册的服务实体
     * @param <T> 服务实体类
     */
    <T> void register(T service);

    /**
     * 根据服务名称获取服务实体
     * @param serviceName 服务名称
     * @return 服务实体
     */
    Object getService(String serviceName);
}
