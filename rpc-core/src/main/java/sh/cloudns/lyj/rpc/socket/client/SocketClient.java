package sh.cloudns.lyj.rpc.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.RpcClient;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import util.ObjectReader;
import util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Description Socket方式远程方法调用的消费者（客户端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketClient implements RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    private final String host;
    private final int port;

    private CommonSerializer serializer;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest request){
        if (serializer == null) {
            LOGGER.error("未设置序列化器");
            throw new RpcException(RpcErrorEnum.SERIALIZER_NOT_FOUND);
        }
        try (Socket socket = new Socket(host, port)) {
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
            // 返回响应结果
            return response.getData();
        } catch (IOException e){
            LOGGER.error("调用时发生错误：", e);
            throw new RpcException("服务调用失败：", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
