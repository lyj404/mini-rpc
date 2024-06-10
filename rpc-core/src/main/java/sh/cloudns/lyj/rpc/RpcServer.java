package sh.cloudns.lyj.rpc;

/**
 * @Description 服务类通用接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface RpcServer {
    /**
     * 启动服务类
     * @param port 端口号
     */
    void start(int port);
}
