package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.net.InetSocketAddress;

/**
 * @Description Nacos 服务注册中心，用于将服务实例注册到 Nacos，以便其他服务能够发现并调用c
 * @Date 2024/6/11
 * @Author lyj
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{
    /**
     * 用于注册服务。
     * @param serviceName 服务名称，用于唯一标识服务。
     * @param inetSocketAddress 服务实例的地址和端口信息。
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            // 使用 NacosUtil 的 registerService 方法将服务注册到 Nacos。
            // 这将 Nacos 服务名与服务实例的地址和端口关联起来。
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e){
            log.error("注册服务时发生错误：", e);
            throw new RpcException(RpcErrorEnum.REGISTER_SERVICE_FAILED);
        }
    }
}
