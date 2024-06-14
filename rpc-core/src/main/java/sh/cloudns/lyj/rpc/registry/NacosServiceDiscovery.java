package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.loadbalancer.LoadBalancer;
import sh.cloudns.lyj.rpc.loadbalancer.RandomLoadBalancer;
import sh.cloudns.lyj.rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/12
 * @Author lyj
 */
public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = Objects.requireNonNullElseGet(loadBalancer, RandomLoadBalancer::new);
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.isEmpty()) {
                LOGGER.error("找不到对应的服务：" + serviceName);
                throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e){
            LOGGER.error("获取服务时发生错误: ", e);
        }
        return null;
    }
}
