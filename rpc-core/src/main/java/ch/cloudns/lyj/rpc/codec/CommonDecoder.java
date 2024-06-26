package ch.cloudns.lyj.rpc.codec;

import ch.cloudns.lyj.rpc.entity.RpcResponse;
import ch.cloudns.lyj.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import ch.cloudns.lyj.rpc.compress.Compress;
import ch.cloudns.lyj.rpc.compress.impl.CompressFactory;
import ch.cloudns.lyj.rpc.entity.RpcRequest;
import ch.cloudns.lyj.rpc.enums.PackageTypeEnum;
import ch.cloudns.lyj.rpc.enums.RpcErrorEnum;
import ch.cloudns.lyj.rpc.exception.RpcException;

import java.util.List;

/**
 * @Description 通用解码拦截器
 * @Date 2024/6/10
 * @Author lyj
 */
@Slf4j
public class CommonDecoder extends ReplayingDecoder {

    /**
     * 定义协议的魔数（Magic Number），用于识别协议的有效性
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final Compress compress = CompressFactory.getCompressInstance(0);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
        // 从 ByteBuf 中读取一个整数作为魔数
        int magic = in.readInt();
        // 检查魔数是否正确，如果不正确则记录错误日志并抛出异常
        if (magic != MAGIC_NUMBER) {
            log.error("不识别的协议包：{}", magic);
            throw new RpcException(RpcErrorEnum.UNKNOWN_PROTOCOL);
        }
        // 读取数据包的类型码
        int packageCode = in.readInt();
        Class<?> packageClass;
        // 根据类型码判断数据包的类型，并设置对应的类对象
        if (packageCode == PackageTypeEnum.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageTypeEnum.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        } else {
            // 如果类型码不匹配，则记录错误日志并抛出异常
            log.error("不识别的整数包：{}", packageCode);
            throw new RpcException(RpcErrorEnum.UNKNOWN_PACKAGE_TYPE);
        }
        // 读取序列化器的类型码
        int serializerCode = in.readInt();
        // 根据类型码获取对应的序列化器实例
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            log.error("不识别的反序列化器：{}", serializerCode);
            throw new RpcException(RpcErrorEnum.UNKNOWN_SERIALIZER);
        }
        // 读取数据包的长度
        int length = in.readInt();
        // 创建一个字节数组，用于存储序列化后的数据
        byte[] bytes = new byte[length];
        // 从 ByteBuf 中读取字节数据到数组
        in.readBytes(bytes);
        // 使用默认压缩器进行解压缩
        bytes = compress.decompress(bytes);
        // 使用序列化器将字节数组反序列化成对象，并添加到输出列表中
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }
}
