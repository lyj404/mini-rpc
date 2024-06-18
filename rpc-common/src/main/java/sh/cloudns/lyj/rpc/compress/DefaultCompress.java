package sh.cloudns.lyj.rpc.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author: liyj
 * @date: 2024/6/18 15:42
 */
public class DefaultCompress implements Compress{

    private static final int BUFFER_SIZE = 4096;

    /**
     * 对输入的字节数组的进行压缩
     * @param bytes 需要被压缩的字节数组
     * @return 压缩后的字节数组
     */
    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("被压缩的数据不能为空");
        }
        // 创建一个Deflater对象用于压缩
        Deflater deflater = new Deflater();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            // 设置要压缩的数据
            deflater.setInput(bytes);
            // 标记压缩数据的结束
            deflater.finish();
            // 创建一个缓冲区，用于存储每次压缩得到的数据
            byte[] buffer = new byte[BUFFER_SIZE];
            // 循环压缩数据，直到Deflater完成压缩
            while (!deflater.finished()){
                // 压缩数据到buffer中，返回压缩的字节数
                int count = deflater.deflate(buffer);
                // 将压缩的字节写入ByteArrayOutputStream
                out.write(buffer, 0, count);
            }
            // 返回压缩后的字节数组
            return out.toByteArray();
        } catch (IOException e){
            throw new RuntimeException("compress error:", e);
        } finally {
            deflater.end();
        }
    }

    /**
     * 对输入的字节数组进行解压缩
     * @param bytes 需要被解压缩的字节数组
     * @return 解压缩后的字节数组
     */
    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("需要解压缩的数据不能为空");
        }
        // 创建一个Inflater对象用于解压缩
        Inflater inflater = new Inflater();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 设置解压缩器的输入为待解压缩的字节数组
            inflater.setInput(bytes);
            // 创建一个缓冲区用于存储解压缩的数据
            byte[] buffer = new byte[BUFFER_SIZE];
            // 循环解压缩数据，直到解压缩器完成
            while (!inflater.finished()){
                // 解压缩数据到缓冲区
                int count = inflater.inflate(buffer);
                // 将解压缩的数据写入ByteArrayOutputStream
                out.write(buffer, 0, count);
            }
            // 返回解压缩后的字节数组
            return out.toByteArray();
        } catch (Exception e){
            throw new RuntimeException("decompress error:", e);
        } finally {
            inflater.end();
        }
    }
}
