package sh.cloudns.lyj.rpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import sh.cloudns.lyj.rpc.enums.SerializerCodeEnum;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description Protobuf序列化器
 * @Date 2024/6/11
 * @Author lyj
 */
public class ProtobufSerializer implements CommonSerializer{
    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public byte[] serialize(Object obj) {
        // 获取待序列化对象的类对象
        Class clazz = obj.getClass();
        // 根据类对象获取对应的 Schema 对象，Schema 用于指导序列化过程
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            // 使用 ProtostuffIOUtil.toByteArray 方法将对象序列化为字节数组
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            // 无论序列化成功与否，都清空缓冲区，以便重用
            buffer.clear();
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        // 根据给定的类对象获取对应的 Schema 实例
        Schema schema = getSchema(clazz);
        // 使用 Schema 创建一个新消息实例，这是反序列化的目标对象
        Object obj = schema.newMessage();
        // ProtostuffIOUtil.mergeFrom 方法将字节数组中的数据反序列化到目标对象中
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getCode() {
        return SerializerCodeEnum.valueOf("PROTOBUF").getCode();
    }

    private Schema getSchema(Class clazz){
        Schema schema = schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            // 通过RuntimeSchema进行懒创建
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)){
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }
}
