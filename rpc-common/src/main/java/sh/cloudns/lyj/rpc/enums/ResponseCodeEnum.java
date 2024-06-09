package sh.cloudns.lyj.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 响应状态码
 * @Date 2024/6/9
 * @Author lyj
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS(200, "执行成功"),
    FAIL(500, "执行失败"),
    METHOD_NOT_FOUND(500, "未找到指定方法"),
    CLASS_NOT_FOUND(500, "未找到指定类");

    private final int code;
    private final String message;
}
