package sh.cloudns.lyj.rpc.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description 读取.properties文件工具类
 * @Date 2024/6/16
 * @Author lyj
 */
@Slf4j
public class PropertiesFileUtil {
    private PropertiesFileUtil(){}

    /**
     * 加载.properties文件到Properties对象
     * @param fileName .properties文件名
     * @return Properties对象
     */
    public static Properties loadProperties(String fileName){
        // 获取当前线程的类加载器资源路径
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        String configPath = "";
        // 如果资源路径不为空，则拼接文件名以形成完整的文件路径
        if (url != null){
            configPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
        }

        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(configPath), StandardCharsets.UTF_8
        )) {
            // 创建Properties对象用于存储属性
            properties = new Properties();
            // 加载输入流中的属性到Properties对象
            properties.load(inputStreamReader);
        } catch (IOException e){
            log.error("读取配置文件[{}]发生错误", fileName);
        }
        return properties;
    }
}
