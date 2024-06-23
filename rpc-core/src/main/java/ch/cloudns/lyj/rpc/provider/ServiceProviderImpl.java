package ch.cloudns.lyj.rpc.provider;

import lombok.extern.slf4j.Slf4j;
import ch.cloudns.lyj.rpc.enums.RpcErrorEnum;
import ch.cloudns.lyj.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 默认的服务注册表，用于存储和管理 RPC 服务的注册信息
 * @Date 2024/6/9
 * @Author lyj
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    /**
     * 用于存储服务对象的映射表，键是服务接口的全类名。
     * ConcurrentHashMap 提供线程安全的键值对存储。
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 用于存储已注册的服务名称的集合，避免重复注册。
     * ConcurrentHashMap.newKeySet() 提供一个线程安全的 Set 集合。
     */
    private static final Set<String> registerService = ConcurrentHashMap.newKeySet();

    /**
     * 向服务注册表中添加服务提供者。
     * @param service 服务实例
     * @param serviceName 服务名称，通常是接口的全类名
     */
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        // 如果服务已注册，则直接返回
        if (registerService.contains(serviceName)) return;
        // 将服务名称添加到已注册服务集合中
        registerService.add(serviceName);
        // 将服务实例添加到服务映射表中
        serviceMap.put(serviceName, service);
        log.info("向接口：{} 注册服务：{}", service.getClass().getInterfaces(), serviceName);
    }

    /**
     * 根据服务名称获取服务提供者实例。
     * @param serviceName 服务名称
     * @return 返回服务实例
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        // 从服务映射表中获取服务实例
        Object service = serviceMap.get(serviceName);
        // 如果服务不存在，则抛出 RpcException 异常
        if (service == null) {
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
        }
        // 返回服务实例
        return service;
    }
}
