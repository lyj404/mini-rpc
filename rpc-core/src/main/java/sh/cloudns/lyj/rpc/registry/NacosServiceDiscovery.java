package sh.cloudns.lyj.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.loadbalancer.LoadBalancer;
import sh.cloudns.lyj.rpc.loadbalancer.impl.RandomLoadBalancer;
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
        // 使用 Objects.requireNonNullElseGet 确保负载均衡器不为 null，如果为 null，则创建一个新的 RandomLoadBalancer 实例
        this.loadBalancer = Objects.requireNonNullElseGet(loadBalancer, RandomLoadBalancer::new);
    }

    /**
     * 根据服务名称查询服务实例的地址信息。
     * @param rpcRequest 请求
     * @return 返回服务实例的 InetSocketAddress。
     */
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getRpcServiceName();
        try {
            // 从 Nacos 获取所有服务实例
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            // 如果没有找到服务实例，记录错误日志并抛出异常
            if (instances.isEmpty()) {
                LOGGER.error("找不到对应的服务：" + serviceName);
                throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
            }
            // 使用负载均衡器从服务实例中选择一个实例
            Instance instance = loadBalancer.select(instances);
            // 返回选中实例的地址和端口信息
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e){
            LOGGER.error("获取服务时发生错误: ", e);
        }
        return null;
    }
}
