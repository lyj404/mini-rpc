package ch.cloudns.lyj.rpc.compress;

/**
 * @Description 压缩接口
 * @Date 2024/6/16
 * @Author lyj
 */
public interface Compress {
    /**
     * 对输入的字节数组的进行压缩
     * @param bytes 需要被压缩的字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 对输入的字节数组进行解压缩
     * @param bytes 需要被解压缩的字节数组
     * @return 解压缩后的字节数组
     */
    byte[] decompress(byte[] bytes);
}
