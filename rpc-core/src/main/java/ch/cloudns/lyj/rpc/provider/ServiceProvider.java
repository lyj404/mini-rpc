package ch.cloudns.lyj.rpc.provider;

/**
 * @Description 保存和提供服务实例对象
 * @Date 2024/6/11
 * @Author lyj
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);
}
