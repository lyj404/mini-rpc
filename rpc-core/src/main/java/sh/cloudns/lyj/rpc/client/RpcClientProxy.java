package sh.cloudns.lyj.rpc.client;

import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description RPC客户端动态代理
 * @Date 2024/6/9
 * @Author lyj
 */
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
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
        // 创建RPC请求实例
        RpcRequest request = RpcRequest.builder()
                // method.getDeclaringClass().getName() 获取被代理接口的名称
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient client = new RpcClient();
        return ((RpcResponse) client.sendRequest(request, host, port)).getData();
    }
}
