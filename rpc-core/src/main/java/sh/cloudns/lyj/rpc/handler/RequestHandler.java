package sh.cloudns.lyj.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import sh.cloudns.lyj.rpc.provider.ServiceProvider;
import sh.cloudns.lyj.rpc.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description 进行过程调用的处理器
 * @Date 2024/6/9
 * @Author lyj
 */
public class RequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    /**
     * 服务提供者，用于获取服务实例
     */
    private static final ServiceProvider SERVICE_PROVIDER;

    static {
        SERVICE_PROVIDER = new ServiceProviderImpl();
    }

    /**
     * 处理 RPC 请求的方法
     * @param rpcRequest RPC请求实体类
     * @return 方法调用的结果
     */
    public Object handle(RpcRequest rpcRequest) {
        // 根据请求中的接口名获取服务实例
        Object service = SERVICE_PROVIDER.getServiceProvider(rpcRequest.getRpcServiceName());
        // 调用 invokeTargetMethod 方法执行具体的服务方法调用
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 调用目标方法
     * @param rpcRequest  RPC请求实体类
     * @param service 服务实例
     * @return 方法调用的结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            // 根据请求中的 方法名 和 参数类型，在服务对象中查找对应的方法
            Method method = service.getClass().getMethod(
                    rpcRequest.getMethodName(), // 方法名
                    rpcRequest.getParamTypes() // 参数类型数组
            );
            // 通过反射调用找到的方法
            result = method.invoke(service, rpcRequest.getParameters());
            LOGGER.info("服务：{} 成功调用方法：{}", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            return RpcResponse.fail(ResponseCodeEnum.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        // 返回方法调用的结果
        return result;
    }
}