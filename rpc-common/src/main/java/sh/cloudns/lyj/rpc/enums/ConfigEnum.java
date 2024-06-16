package sh.cloudns.lyj.rpc.enums;

import lombok.Getter;

/**
 * @Description 配置枚举
 * @Date 2024/6/16
 * @Author lyj
 */
@Getter
public enum ConfigEnum {
    /**
     * 配置文件路径
     */
    CONFIG_PATH("rpc.properties"),
    /**
     * nacos地址
     */
    NACOS_ADDRESS("nacos.address");

    ConfigEnum(String value) {
        this.value = value;
    }

    private final String value;
}
