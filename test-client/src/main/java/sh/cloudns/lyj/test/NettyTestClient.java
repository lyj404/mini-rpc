package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.RpcClient;
import sh.cloudns.lyj.rpc.transport.RpcClientProxy;
import sh.cloudns.lyj.rpc.transport.netty.client.NettyClient;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
