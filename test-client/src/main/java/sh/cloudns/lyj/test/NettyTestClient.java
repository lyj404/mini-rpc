package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.transport.RpcClientProxy;
import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.transport.netty.client.NettyClient;
import sh.cloudns.lyj.rpc.serializer.ProtobufSerializer;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
