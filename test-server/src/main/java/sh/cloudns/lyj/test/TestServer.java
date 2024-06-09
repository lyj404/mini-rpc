package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.registry.DefaultServiceRegistry;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;
import sh.cloudns.lyj.rpc.server.RpcServer;

/**
 * @Description 测试用的服务提供方（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
