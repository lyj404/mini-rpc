package sh.cloudns.lyj.rpc.enums;

import lombok.Getter;

/**
 * @Description 压缩类型枚举
 * @Date 2024/6/16
 * @Author lyj
 */
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip"),
    ;

    CompressTypeEnum(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    private final byte code;
    private final String name;

    /**
     * 根据code获取name
     * @param code 编码
     * @return 压缩类型名
     */
    public static String getName(byte code){
        for (var compressTypeEnum : CompressTypeEnum.values()) {
            if (compressTypeEnum.getCode() == code) {
                return compressTypeEnum.name;
            }
        }
        return null;
    }
}