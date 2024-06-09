package sh.cloudns.lyj.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description RPC错误代码枚举类
 * @Date 2024/6/9
 * @Author lyj
 */
@Getter
@AllArgsConstructor
public enum RpcErrorEnum {
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口");

    private final String message;
}
