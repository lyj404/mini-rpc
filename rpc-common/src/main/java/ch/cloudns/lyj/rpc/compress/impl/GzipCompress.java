package ch.cloudns.lyj.rpc.compress.impl;

import ch.cloudns.lyj.rpc.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description 使用Gzip实现压缩接口
 * @Date 2024/6/16
 * @Author lyj
 */
public class GzipCompress implements Compress {

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
        // ByteArrayOutputStream用于捕获GZIPOutputStream的输出，即压缩后的数据
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            // 将输入的字节数组写入GZIPOutputStream进行压缩
            gzip.write(bytes);
            // 刷新GZIPOutputStream，确保所有数据都被写出
            gzip.flush();
            // 完成压缩过程
            gzip.finish();
            // 将压缩后的数据转换为字节数组并返回
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip compress error:", e);
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
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream unzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            // 创建一个大小为BUFFER_SIZE的缓冲区，用于读取解压缩的数据
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            // 循环读取GZIPInputStream中的数据到缓冲区，直到读取完所有数据
            while ((n = unzip.read(buffer)) > -1) {
                // 将缓冲区中的数据写入ByteArrayOutputStream
                out.write(buffer, 0, n);
            }
            // 将解压缩后的数据转换为字节数组并返回
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip decompress error:", e);
        }
    }
}
