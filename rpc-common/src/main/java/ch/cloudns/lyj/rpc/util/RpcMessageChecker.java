package ch.cloudns.lyj.rpc.util;

import ch.cloudns.lyj.rpc.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import ch.cloudns.lyj.rpc.entity.RpcRequest;
import ch.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import ch.cloudns.lyj.rpc.enums.RpcErrorEnum;
import ch.cloudns.lyj.rpc.exception.RpcException;

/**
 * @Description 检查请求与响应的消息
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class RpcMessageChecker {
    private static final String INTERFACE_NAME = "interfaceName";
    private RpcMessageChecker(){}

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        // 判断响应是否为空
        if (rpcResponse == null) {
            log.error("调用服务失败, serviceName: {}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + "：" +
                    rpcRequest.getInterfaceName());
        }
        // 判断请求和响应的ID是否相等
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorEnum.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" +
                    rpcRequest.getInterfaceName());
        }
        // 判断响应状态是否是成功状态
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCodeEnum.SUCCESS.getCode())){
            log.error("调用服务失败, serviceName: {}, RpcResponse: {}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + "：" +
                    rpcRequest.getInterfaceName());
        }
    }
}
