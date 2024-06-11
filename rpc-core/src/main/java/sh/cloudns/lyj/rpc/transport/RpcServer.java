package sh.cloudns.lyj.rpc.transport;

import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

/**
 * @Description 服务类通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcServer {
    /**
     * 启动服务类
     */
    void start();

    /**
     * 设置序列化器
     * @param serializer 序列化器
     */
    void setSerializer(CommonSerializer serializer);

    /**
     * 发布服务
     * @param service 服务
     * @param serviceClass 服务的类
     * @param <T> 泛型
     */
    <T> void publishService(Object service, Class<T> serviceClass);
}
