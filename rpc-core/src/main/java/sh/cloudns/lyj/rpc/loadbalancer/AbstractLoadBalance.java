package sh.cloudns.lyj.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.util.List;

/**
 * @author: liyj
 * @date: 2024/6/20 15:32
 */
public abstract class AbstractLoadBalance implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        if (instances == null || instances.isEmpty()) {
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return doSelect(instances);
    }

    protected abstract Instance doSelect(List<Instance> instances);
}
