package sh.cloudns.lyj.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description 进行过程调用的处理器
 * @Date 2024/6/9
 * @Author lyj
 */
public class RequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = this.invokeTargetMethod(rpcRequest, service);
            LOGGER.info("服务：{}成功调用方法：{}", rpcRequest.getInterfaceName(),
                    rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e){
            LOGGER.error("调用或发送时有错误发生：", e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException{
        Method method;
        try {
            // 根据请求中的 方法名 和 参数类型，在服务对象中查找对应的方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(),
                    rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCodeEnum.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
