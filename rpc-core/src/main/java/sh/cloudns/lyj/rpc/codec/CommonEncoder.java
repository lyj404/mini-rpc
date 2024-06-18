package sh.cloudns.lyj.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import sh.cloudns.lyj.rpc.compress.Compress;
import sh.cloudns.lyj.rpc.compress.CompressFactory;
import sh.cloudns.lyj.rpc.entity.RpcRequest;
import sh.cloudns.lyj.rpc.enums.PackageTypeEnum;
import sh.cloudns.lyj.rpc.serializer.CommonSerializer;

/**
 * @Description 通用编码拦截器
 * @Date 2024/6/10
 * @Author lyj
 */
public class CommonEncoder extends MessageToByteEncoder {

    /**
     * 定义协议的魔数（Magic Number），用于识别协议的有效性
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    private final Compress compress = CompressFactory.getCompressInstance(0);

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out){
        // 写入魔数
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest){
            // 指定数据包类型
            out.writeInt(PackageTypeEnum.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageTypeEnum.RESPONSE_PACK.getCode());
        }
        // 指定解码类型
        out.writeInt(serializer.getCode());
        // 对消息进行序列化
        byte[] bytes = serializer.serialize(msg);
        // 使用默认压缩器压缩消息
        bytes = compress.compress(bytes);
        // 写入消息长度
        out.writeInt(bytes.length);
        // 写入消息
        out.writeBytes(bytes);
    }
}
