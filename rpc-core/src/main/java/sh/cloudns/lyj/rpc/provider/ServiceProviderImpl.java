package sh.cloudns.lyj.rpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 默认的服务注册表
 * @Date 2024/6/9
 * @Author lyj
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProviderImpl.class);

    /**
     * 用于存储服务对象，键是服务接口的全类名
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 用于存储已注册的服务名称，避免重复注册
     */
    private static final Set<String> registerService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service) {
        // 获取服务对象的全类名，作为服务名
        String serviceName = service.getClass().getCanonicalName();
        // 如果服务已注册，则直接返回
        if (registerService.contains(serviceName)) return;
        registerService.add(serviceName);
        // 获取服务类实现的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0){
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        for (var i : interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        LOGGER.info("向接口：{} 注册服务：{}", interfaces, serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
