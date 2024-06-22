package sh.cloudns.lyj.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 配置枚举
 * @Date 2024/6/16
 * @Author lyj
 */
@Getter
@AllArgsConstructor
public enum ConfigEnum {
    /**
     * 配置文件路径
     */
    CONFIG_PATH("rpc.properties"),
    /**
     * nacos地址
     */
    NACOS_ADDRESS("nacos.address"),
    HOST("host"),
    PORT("port"),
    ;

    private final String value;
}
