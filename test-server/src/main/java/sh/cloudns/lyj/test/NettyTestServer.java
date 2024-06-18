package sh.cloudns.lyj.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sh.cloudns.lyj.rpc.annotation.ServiceScan;
import sh.cloudns.lyj.rpc.transport.netty.server.NettyServer;

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
