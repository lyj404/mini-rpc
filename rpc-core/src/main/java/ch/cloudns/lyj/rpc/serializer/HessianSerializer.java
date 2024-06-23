package ch.cloudns.lyj.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;
import ch.cloudns.lyj.rpc.enums.SerializerCodeEnum;
import ch.cloudns.lyj.rpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Description Hessian协议的序列化器
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class HessianSerializer implements CommonSerializer{

    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e){
            log.error("序列化时发生错误: ", e);
            throw new SerializeException("序列化时发生错误");
        } finally {
            if (hessianOutput != null){
                try {
                    hessianOutput.close();
                } catch (IOException e){
                    log.error("关闭Hessian时发生错误：", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e){
            log.error("反序列化时发生错误: ", e);
            throw new SerializeException("反序列化时发生错误");
        } finally {
            if (hessianInput != null){
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCodeEnum.valueOf("HESSIAN").getCode();
    }
}
