package sh.cloudns.lyj.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Description 远程方法调用的消费者（客户端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest request, String host, int port){
        try (Socket socket = new Socket(host, port)) {
            // 创建一个输出流，用于向服务器发送请求对象
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 创建一个输入流，用于从服务器接收响应对象
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 将请求对象写入输出流，以便发送到服务器
            objectOutputStream.writeObject(request);
            // 刷新输出流，确保请求对象被发送出去
            objectOutputStream.flush();
            // 从输入流中读取服务器的响应对象
            RpcResponse response = (RpcResponse) objectInputStream.readObject();
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
        } catch (IOException | ClassNotFoundException e){
            LOGGER.error("调用时发生错误：", e);
            throw new RpcException("服务调用失败：", e);
        }
    }
}
