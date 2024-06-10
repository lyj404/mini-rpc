package sh.cloudns.lyj.rpc;

import sh.cloudns.lyj.rpc.entity.RpcRequest;

/**
 * @Description 客户端通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcClient {
    /**
     * 发送请求
     * @param rpcRequest 请求实体类
     * @return 响应结果
     */
    Object sendRequest(RpcRequest rpcRequest);
}
