package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.net.InetSocketAddress;

/**
 * @Description Nacos 服务注册中心，用于将服务实例注册到 Nacos，以便其他服务能够发现并调用c
 * @Date 2024/6/11
 * @Author lyj
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

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
            LOGGER.error("注册服务时发生错误：", e);
            throw new RpcException(RpcErrorEnum.REGISTER_SERVICE_FAILED);
        }
    }
}
