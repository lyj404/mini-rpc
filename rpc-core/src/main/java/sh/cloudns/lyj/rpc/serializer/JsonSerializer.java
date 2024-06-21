package sh.cloudns.lyj.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.enums.SerializerCodeEnum;
import sh.cloudns.lyj.rpc.exception.SerializeException;

import java.io.IOException;

/**
 * @Description JSON格式序列化
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class JsonSerializer implements CommonSerializer{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e){
            log.error("序列化时产生错误：{}", e.getMessage());
            throw new SerializeException("序列化时产生错误");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e){
            log.error("序列化时产生错误：{}", e.getMessage());
            throw new SerializeException("序列化时产生错误");
        }
    }

    @Override
    public int getCode(){
        return SerializerCodeEnum.valueOf("JSON").getCode();
    }

    /**
     * 保证反序列化后还是原实例类型。
     * 此方法用于处理对象，确保对象的字段在反序列化后与其在请求中声明的类型完全匹配。
     * @param obj 需要反序列化的对象，这里期望是一个 RpcRequest 实例。
     * @return 处理后的对象，保证其字段类型与原始请求中的类型声明一致。
     * @throws IOException 如果序列化或反序列化过程中发生错误，将抛出 IOException。
     */
    private Object handleRequest(Object obj) throws IOException{
        // 将传入的对象强制转换为 RpcRequest 类型
        RpcRequest rpcRequest = (RpcRequest) obj;

        // 遍历 RpcRequest 中的参数类型和参数值
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++){
            Class<?> paramType = rpcRequest.getParamTypes()[i]; // 获取当前参数的类型
            // 检查当前参数的运行时类是否与声明的类型匹配
            if (!paramType.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                // 如果不匹配，使用 objectMapper 将参数值序列化为字节数组
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                // 然后使用 objectMapper 将字节数组反序列化为正确的类型
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, paramType);
            }
        }
        // 返回处理后的 RpcRequest 对象
        return rpcRequest;
    }
}
