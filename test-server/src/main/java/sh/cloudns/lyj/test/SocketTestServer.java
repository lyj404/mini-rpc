package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.annotation.ServiceScan;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.RpcServer;
import sh.cloudns.lyj.rpc.transport.socket.server.SocketServer;

/**
 * @Description 测试用的服务提供方（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9999, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }
}
