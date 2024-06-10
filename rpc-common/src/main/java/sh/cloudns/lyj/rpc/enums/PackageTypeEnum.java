package sh.cloudns.lyj.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Date 2024/6/9
 * @Author lyj
 */
@Getter
@AllArgsConstructor
public enum PackageTypeEnum {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
