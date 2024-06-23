package ch.cloudns.lyj.rpc.transport;

/**
 * @Description RPC服务类通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcServer {

    /**
     * 启动服务类
     */
    void start();

    /**
     * 发布服务
     * @param service 服务
     * @param serviceName 服务的类
     * @param <T> 泛型
     */
    <T> void publishService(T service, String serviceName);
}
