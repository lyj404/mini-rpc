package ch.cloudns.lyj.test;

import ch.cloudns.lyj.rpc.api.HelloObject;
import ch.cloudns.lyj.rpc.api.HelloService;
import ch.cloudns.lyj.rpc.serializer.CommonSerializer;
import ch.cloudns.lyj.rpc.transport.RpcClientProxy;
import ch.cloudns.lyj.rpc.transport.RpcServiceConfig;
import ch.cloudns.lyj.rpc.transport.socket.client.SocketClient;

/**
 * @Description 测试用的消费者（客户端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client, new RpcServiceConfig("Group1", HelloService.class));
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
