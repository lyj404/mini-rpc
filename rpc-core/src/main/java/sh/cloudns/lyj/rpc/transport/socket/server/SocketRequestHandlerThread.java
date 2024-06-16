package sh.cloudns.lyj.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.handler.RequestHandler;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.socket.util.ObjectReader;
import sh.cloudns.lyj.rpc.transport.socket.util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Description 处理RpcRequest的工作线程
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketRequestHandlerThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private final Socket socket;
    private final RequestHandler requestHandler;
    private final CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    /**
     * 实现 Runnable 接口的 run 方法，处理接收到的请求
     */
    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            // 从客户端读取 RpcRequest 对象
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            // 获取调用的接口名
            Object result = requestHandler.handle(rpcRequest);
            // 创建 RpcResponse 对象，包含处理结果和请求 ID
            RpcResponse<Object> response = RpcResponse.success(result,rpcRequest.getRequestId());
            // 序列化 RpcResponse 对象并写入输出流，发送回客户端
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e){
            LOGGER.error("调用或发送时有错误发生:", e);
        }
    }
}
