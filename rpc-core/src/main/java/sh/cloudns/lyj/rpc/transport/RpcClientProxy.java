package sh.cloudns.lyj.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.transport.netty.client.NettyClient;
import sh.cloudns.lyj.rpc.transport.socket.client.SocketClient;
import sh.cloudns.lyj.rpc.util.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    /**
     * 获取代理对象的方法，用于获取指定接口的代理实例。
     *
     * @param <T> 接口类型
     * @param clazz 接口的Class对象
     * @return 代理对象
     */
    @SuppressWarnings("unchecked") // 抑制未经检查的转换警告
    public <T> T getProxy(Class<T> clazz){
        // 使用 Proxy 类的静态方法 newProxyInstance 创建一个新的代理实例
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(), // 获取指定接口的类加载器
                new Class<?>[]{clazz}, // 代理的接口数组，这里只代理一个接口
                this // 将当前 RpcClientProxy 实例作为调用处理器传入
        );
    }

    /**
     * InvocationHandler 的 invoke 方法，用于处理代理对象的方法调用。
     *
     * @param proxy 代理对象
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法调用结果
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        LOGGER.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
        // 创建RPC请求实例
        RpcRequest rpcRequest = new RpcRequest(
                // 请求ID
                UUID.randomUUID().toString(),
                // 接口名称
                method.getDeclaringClass().getName(),
                // 方法名称
                method.getName(),
                // 方法参数
                args,
                // 方法参数类型
                method.getParameterTypes(),
                // 是否为心跳请求
                false);
        RpcResponse<?> rpcResponse = null;
        // 根据 RpcClient 类型进行请求发送
        if (client instanceof NettyClient) {
            try {
                // Netty 客户端，使用异步发送请求
                CompletableFuture<RpcResponse<?>> completableFuture = (CompletableFuture<RpcResponse<?>>) client.sendRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            } catch (Exception e) {
                LOGGER.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            // 基于Socket的客户端，同步发送请求
            rpcResponse = (RpcResponse<?>) client.sendRequest(rpcRequest);
        }
        // 检查响应是否有效
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        assert rpcResponse != null;
        // 返回方法调用结果
        return rpcResponse.getData();
    }
}
