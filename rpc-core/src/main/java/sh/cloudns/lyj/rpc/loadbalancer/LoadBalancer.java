package sh.cloudns.lyj.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author: liyj
 * @date: 2024/6/13 17:31
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
