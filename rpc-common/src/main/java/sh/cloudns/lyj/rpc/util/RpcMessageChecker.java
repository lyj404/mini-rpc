package sh.cloudns.lyj.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

/**
 * @Description 检查请求与响应的消息
 * @Date 2024/6/10
 * @Author lyj
 */
public class RpcMessageChecker {
    private static final String INTERFACE_NAME = "interfaceName";
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker(){}

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if (rpcResponse == null) {
            LOGGER.error("调用服务失败, serviceName: {}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + "：" +
                    rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorEnum.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" +
                    rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCodeEnum.SUCCESS.getCode())){
            LOGGER.error("调用服务失败, serviceName: {}, RpcResponse: {}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + "：" +
                    rpcRequest.getInterfaceName());
        }
    }
}
