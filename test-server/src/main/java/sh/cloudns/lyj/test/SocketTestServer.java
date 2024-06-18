package sh.cloudns.lyj.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sh.cloudns.lyj.rpc.annotation.ServiceScan;
import sh.cloudns.lyj.rpc.transport.socket.server.SocketServer;

/**
 * @Description 测试用的服务提供方（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SocketServer.class);
        SocketServer socketServer = (SocketServer) applicationContext.getBean("socketServer");
        socketServer.registerService();
        socketServer.start();
    }
}
