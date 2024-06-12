package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.serializer.HessianSerializer;
import sh.cloudns.lyj.rpc.transport.socket.server.SocketServer;

/**
 * @Description 测试用的服务提供方（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9999);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }
}
