package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Date 2024/6/12
 * @Author lyj
 */
public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private final NamingService namingService;

    public NacosServiceDiscovery() {
        this.namingService = NacosUtil.getNacosNamingService();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(namingService, serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e){
            LOGGER.error("获取服务时发生错误: ", e);
        }
        return null;
    }
}
