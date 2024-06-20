package sh.cloudns.lyj.rpc.transport.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.loadbalancer.LoadBalancer;
import sh.cloudns.lyj.rpc.loadbalancer.impl.RandomLoadBalancer;
import sh.cloudns.lyj.rpc.registry.NacosServiceDiscovery;
import sh.cloudns.lyj.rpc.registry.ServiceDiscovery;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.RpcClient;
import sh.cloudns.lyj.rpc.transport.socket.util.ObjectReader;
import sh.cloudns.lyj.rpc.transport.socket.util.ObjectWriter;
import sh.cloudns.lyj.rpc.util.RpcMessageChecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description Socket方式远程方法调用的消费者（客户端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketClient implements RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    private final ServiceDiscovery serviceDiscovery;

    private final CommonSerializer serializer;

    public SocketClient(){
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public SocketClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequest request){
        if (serializer == null) {
            LOGGER.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            // 创建一个输出流，用于向服务器发送请求对象
            OutputStream outputStream = socket.getOutputStream();
            // 创建一个输入流，用于从服务器接收响应对象
            InputStream inputStream = socket.getInputStream();
            // 将请求对象写入输出流
            ObjectWriter.writeObject(outputStream, request, serializer);
            // 从输入流中读取服务器的响应对象
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse response = (RpcResponse) obj;
            // 判断响应对象是否为空
            if (response == null) {
                LOGGER.error("服务调用失败, service：{}",request.getInterfaceName());
                throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, " service:" + request.getInterfaceName());
            }

            // 判断响应结果的状态码是否是成功的状态码
            if (response.getStatusCode() == null || response.getStatusCode() != ResponseCodeEnum.SUCCESS.getCode()){
                LOGGER.error("服务调用失败, service：{}, response: {}",request.getInterfaceName(), response);
                throw new RpcException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE, " service:" + request.getInterfaceName());
            }
            // 检查RPC的请求和响应
            RpcMessageChecker.check(request, response);
            // 返回响应结果
            return response;
        } catch (IOException e){
            LOGGER.error("调用时发生错误：", e);
            throw new RpcException("服务调用失败：", e);
        }
    }
}
