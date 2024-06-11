package sh.cloudns.lyj.rpc.transport.socket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.entity.RpcResponse;
import sh.cloudns.lyj.rpc.enums.PackageTypeEnum;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description Socket方式从输入流中读取字节并反序列化
 * @Date 2024/6/10
 * @Author lyj
 */
public class ObjectReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     * readObject 方法从给定的 InputStream 中读取字节并反序列化成对象。
     * @param in 输入流，包含要反序列化的字节数据
     * @return 反序列化后的对象
     * @throws IOException 如果读取或反序列化过程中发生 I/O 错误
     */
    public static Object readObject(InputStream in) throws IOException{
        // 读取前4个字节，转换成int类型的魔数
        byte[] numberBytes = new byte[4];
        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        // 验证魔数是否正确，如果不正确则记录错误日志并抛出异常
        if (magic != MAGIC_NUMBER) {
            LOGGER.error("不识别的协议包：{}", magic);
            throw new RpcException(RpcErrorEnum.UNKNOWN_PROTOCOL);
        }
        // 读取包类型码
        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        // 根据类型码判断数据包的类型，并设置对应的类对象
        if (packageCode == PackageTypeEnum.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageTypeEnum.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            // 如果类型码不匹配，则记录错误日志并抛出异常
            LOGGER.error("不识别的数据包：{}", packageCode);
            throw new RpcException(RpcErrorEnum.UNKNOWN_PACKAGE_TYPE);
        }
        // 读取序列化器的类型码
        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        // 根据类型码获取对应的序列化器实例
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            // 如果没有找到对应的序列化器，则记录错误日志并抛出异常
            LOGGER.error("不识别的反序列化器：{}", serializerCode);
            throw new RpcException(RpcErrorEnum.UNKNOWN_SERIALIZER);
        }
        // 读取数据长度
        in.read(numberBytes);
        int length = bytesToInt(numberBytes);
        // 创建一个字节数组，用于存储序列化后的数据
        byte[] bytes = new byte[length];
        // 从 InputStream 中读取字节数据到数组
        in.read(bytes);
        // 使用序列化器将字节数组反序列化成对象，并返回
        return serializer.deserialize(bytes, packageClass);
    }

    /**
     * bytesToInt 方法将4字节的字节数组转换为int类型的数值。
     * @param src 字节数组，必须是长度为4 的数组
     * @return 转换得到的int数值
     */
    public static int bytesToInt(byte[] src) {
        int value;
        // 将字节数组中的每个字节转换为int，并按顺序拼接成int类型的数值
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }
}
