package sh.cloudns.lyj.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Description 调用过程中的工作线程
 * @Date 2024/6/9
 * @Author lyj
 */
public class WorkerThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            // 从客户端读取 RpcRequest 对象
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 根据请求中的 方法名 和 参数类型，在服务对象中查找对应的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            // 调用查找到的方法，并传入请求中的参数，获取返回值
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            // 将方法调用的结果封装为 RpcResponse 对象，并发送给客户端
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            LOGGER.error("调用或发送时有错误发生：", e);
        }
    }
}
