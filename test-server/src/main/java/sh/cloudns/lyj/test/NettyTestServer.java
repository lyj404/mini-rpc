package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.netty.server.NettyServer;
import sh.cloudns.lyj.rpc.registry.DefaultServiceRegistry;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
