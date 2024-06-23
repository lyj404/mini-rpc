package ch.cloudns.lyj.rpc.transport;

import ch.cloudns.lyj.rpc.entity.RpcRequest;
import ch.cloudns.lyj.rpc.serializer.CommonSerializer;

/**
 * @Description RPC客户端通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    /**
     * 发送请求
     * @param rpcRequest 请求实体类
     * @return 响应结果
     */
    Object sendRequest(RpcRequest rpcRequest);
}
