package sh.cloudns.lyj.rpc.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: liyj
 * @date: 2024/6/20 19:32
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {


    /**
     * 当接口有多个实现类时，通过分组标识进行区分
     */
    private String group = "";

    /**
     * 目标服务实例
     */
    private Object service;

    /**
     * 获取 RPC 服务的名称
     * @return 返回完整的 RPC 服务名称
     */
    public String getRpcServiceName() {
        log.info("getRpcServiceName: {}", this.getInterfaceName() + this.getGroup());
        return this.getInterfaceName() + this.getGroup();
    }

    /**
     * 获取目标服务实现的第一个接口的全限定名
     * @return 返回目标服务实现的接口的全限定名
     */
    public String getInterfaceName(){
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
