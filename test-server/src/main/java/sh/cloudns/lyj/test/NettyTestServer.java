package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.annotation.ServiceScan;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;
import sh.cloudns.lyj.rpc.transport.RpcServer;
import sh.cloudns.lyj.rpc.transport.netty.server.NettyServer;

/**
 * @Date 2024/6/10
 * @Author lyj
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
