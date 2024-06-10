package sh.cloudns.lyj.rpc.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.RequestHandler;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import util.ObjectReader;
import util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Description 处理RpcRequest的工作线程
 * @Date 2024/6/9
 * @Author lyj
 */
public class RequestHandlerThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            // 从客户端读取 RpcRequest 对象
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            // 获取调用的接口名
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            // 获取响应结果
            Object result = requestHandler.handle(rpcRequest, service);
            RpcResponse<Object> response = RpcResponse.success(result);
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e){
            LOGGER.error("调用或发送时有错误发生:", e);
        }
    }
}
