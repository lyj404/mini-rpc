package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.server.RpcServer;

/**
 * @Description 测试用的服务提供方（服务端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
