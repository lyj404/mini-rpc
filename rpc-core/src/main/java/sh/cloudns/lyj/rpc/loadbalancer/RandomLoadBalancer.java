package sh.cloudns.lyj.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * @author: liyj
 * @date: 2024/6/13 17:33
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        // 使用 L64X128MixRandom 随机数生成器
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("L64X128MixRandom").create();
        return instances.get(randomGenerator.nextInt(instances.size()));
    }
}