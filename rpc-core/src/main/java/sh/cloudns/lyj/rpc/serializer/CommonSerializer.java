package sh.cloudns.lyj.rpc.serializer;

/**
 * @Description 通用序列化反序列化接口
 * @Date 2024/6/10
 * @Author lyj
 */
public interface CommonSerializer {
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
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            default:
                return null;
        }
    }

}
