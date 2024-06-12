package sh.cloudns.lyj.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Description RPC客户端动态代理
 * @Date 2024/6/9
 * @Author lyj
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked") // 抑制未经检查的转换警告
    public <T> T getProxy(Class<T> clazz){
        // 使用 Proxy 类的静态方法 newProxyInstance 创建一个新的代理实例
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(), // 获取指定接口的类加载器
                new Class<?>[]{clazz}, // 代理的接口数组，这里只代理一个接口
                this // 将当前 RpcClientProxy 实例作为调用处理器传入
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOGGER.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
        // 创建RPC请求实例
        RpcRequest request = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        return client.sendRequest(request);
    }
}