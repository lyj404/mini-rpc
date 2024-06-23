package ch.cloudns.lyj.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 消费者向提供者发送的请求对象
 * @Date 2024/6/9
 * @Author lyj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 被调用的接口名称
     */
    private String interfaceName;

    /**
     * 被调用方面的名称
     */
    private String methodName;

    /**
     * 被调用方面的参数
     */
    private Object[] parameters;

    /**
     * 被调用方面的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;

    /**
     * 服务的分组，用于区分同一个接口的多个实现类
     */
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup();
    }
}