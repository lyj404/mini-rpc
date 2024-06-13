package sh.cloudns.lyj.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 消费者向提供者发送的请求对象
 * @Date 2024/6/9
 * @Author lyj
 */
@Data
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
}