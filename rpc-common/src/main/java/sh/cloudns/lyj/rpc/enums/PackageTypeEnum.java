package sh.cloudns.lyj.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 包类型枚举
 * @Date 2024/6/9
 * @Author lyj
 */
@Getter
@AllArgsConstructor
public enum PackageTypeEnum {
    /**
     * 请求包
     */
    REQUEST_PACK(0),
    /**
     * 响应包
     */
    RESPONSE_PACK(1);

    private final int code;
}
