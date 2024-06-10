package sh.cloudns.lyj.rpc;

import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

/**
 * @Description 服务类通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcServer {
    /**
     * 启动服务类
     * @param port 端口号
     */
    void start(int port);

    /**
     * 设置序列化器
     * @param serializer 序列化器
     */
    void setSerializer(CommonSerializer serializer);
}
