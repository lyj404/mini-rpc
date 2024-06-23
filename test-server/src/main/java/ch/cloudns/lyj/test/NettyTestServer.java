package ch.cloudns.lyj.test;

import ch.cloudns.lyj.rpc.annotation.ServiceScan;
import ch.cloudns.lyj.rpc.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServer.class);
        NettyServer nettyServer = (NettyServer) applicationContext.getBean("nettyServer");
        nettyServer.registerService();
        nettyServer.start();
    }
}
