package ch.cloudns.lyj.rpc.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import ch.cloudns.lyj.rpc.loadbalancer.AbstractLoadBalance;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * @Description 随机负载均衡
 * @author: lyj
 * @date: 2024/6/13 17:33
 */
public class RandomLoadBalancer extends AbstractLoadBalance {
    @Override
    protected Instance doSelect(List<Instance> instances) {
        // 使用 L64X128MixRandom 随机数生成器
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("L64X128MixRandom").create();
        return instances.get(randomGenerator.nextInt(instances.size()));
    }
}
