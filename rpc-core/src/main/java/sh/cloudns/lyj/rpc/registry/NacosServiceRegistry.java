package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description Nacos服务注册中心
 * @Date 2024/6/11
 * @Author lyj
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService NAMING_SERVICE;

    static {
        try {
            NAMING_SERVICE = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e){
            LOGGER.error("连接到Nacos时发生错误：", e);
            throw new RpcException(RpcErrorEnum.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NAMING_SERVICE.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e){
            LOGGER.error("注册服务时发生错误：", e);
            throw new RpcException(RpcErrorEnum.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NAMING_SERVICE.getAllInstances(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e){
            LOGGER.error("获取服务时发生错误：", e);
        }
        return null;
    }
}
