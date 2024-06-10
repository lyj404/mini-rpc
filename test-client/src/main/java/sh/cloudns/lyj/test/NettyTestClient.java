package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.RpcClientProxy;
import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.netty.client.NettyClient;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
