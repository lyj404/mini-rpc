package ch.cloudns.lyj.rpc.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import ch.cloudns.lyj.rpc.loadbalancer.AbstractLoadBalance;

import java.util.List;

/**
 * @Description 轮询负载均衡
 * @author: lyj
 * @date: 2024/6/13 17:34
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected Instance doSelect(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
