package sh.cloudns.lyj.rpc.serializer;

/**
 * @Description 通用序列化反序列化接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = JSON_SERIALIZER;

    /**
     * 序列化
     * @param obj 需要序列化的对象
     * @return 序列化之后的数据
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 需要反序列化的数据
     * @param clazz 反序列化之后的对象类型
     * @return 反序列化的之后的对象
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code){
        return switch (code) {
            case 0 -> new KryoSerializer();
            case 1 -> new JsonSerializer();
            case 2 -> new HessianSerializer();
            case 3 -> new ProtobufSerializer();
            default -> null;
        };
    }

}
