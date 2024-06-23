package ch.cloudns.lyj.test;

import ch.cloudns.lyj.rpc.api.HelloObject;
import ch.cloudns.lyj.rpc.api.HelloService;
import ch.cloudns.lyj.rpc.serializer.CommonSerializer;
import ch.cloudns.lyj.rpc.transport.RpcClient;
import ch.cloudns.lyj.rpc.transport.RpcClientProxy;
import ch.cloudns.lyj.rpc.transport.RpcServiceConfig;
import ch.cloudns.lyj.rpc.transport.netty.client.NettyClient;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client, new RpcServiceConfig("Group1", HelloService.class));
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
