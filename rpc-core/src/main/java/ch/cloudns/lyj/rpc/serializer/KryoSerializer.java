package ch.cloudns.lyj.rpc.serializer;

import ch.cloudns.lyj.rpc.entity.RpcResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import ch.cloudns.lyj.rpc.entity.RpcRequest;
import ch.cloudns.lyj.rpc.enums.SerializerCodeEnum;
import ch.cloudns.lyj.rpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Description Kryo序列化器
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class KryoSerializer implements CommonSerializer{

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        // 创建 Kryo 实例
        Kryo kryo = new Kryo();
        // 注册RpcRequest和RpcResponse类，以便 Kryo 可以序列化和反序列化该类的实例
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 启用引用跟踪，避免重复序列化相同的对象
        kryo.setReferences(true);
        // 设置为 false，允许序列化未注册的类
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 使用 Kryo 序列化对象
            kryo.writeObject(output, obj);
            // 从线程局部变量中移除 Kryo 实例，以确保不会重复使用
            KRYO_THREAD_LOCAL.remove();
            // 返回序列化后的字节数组
            return output.toBytes();
        } catch (Exception e){
            log.error("序列化是发生错误：", e);
            throw new SerializeException("序列化时发生错误");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 使用 Kryo 反序列化对象
            Object object = kryo.readObject(input, clazz);
            KRYO_THREAD_LOCAL.remove();
            return object;
        } catch (Exception e){
            log.error("发序列化是发生错误：", e);
            throw new SerializeException("发序列化时发生错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerCodeEnum.valueOf("KRYO").getCode();
    }
}
