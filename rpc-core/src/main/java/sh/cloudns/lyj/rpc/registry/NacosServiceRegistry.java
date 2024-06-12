package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.net.InetSocketAddress;

/**
 * @Description Nacos服务注册中心
 * @Date 2024/6/11
 * @Author lyj
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e){
            LOGGER.error("注册服务时发生错误：", e);
            throw new RpcException(RpcErrorEnum.REGISTER_SERVICE_FAILED);
        }
    }
}
