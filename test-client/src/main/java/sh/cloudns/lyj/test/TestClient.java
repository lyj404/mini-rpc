package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;
import sh.cloudns.lyj.rpc.client.RpcClientProxy;

/**
 * @Description 测试用的消费者（客户端）
 * @Date 2024/6/9
 * @Author lyj
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
