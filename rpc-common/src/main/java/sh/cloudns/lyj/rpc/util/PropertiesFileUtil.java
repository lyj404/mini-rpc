package sh.cloudns.lyj.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description 读取.properties文件工具类
 * @Date 2024/6/16
 * @Author lyj
 */
public class PropertiesFileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileUtil.class);

    private PropertiesFileUtil(){}

    /**
     * 加载.properties文件到Properties对象
     * @param fileName .properties文件名
     * @return Properties对象
     */
    public static Properties loadProperties(String fileName){
        // 获取当前线程的类加载器资源路径
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String configPath = "";
        // 如果资源路径不为空，则拼接文件名以形成完整的文件路径
        if (url != null){
            configPath = url.getPath() + fileName;
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
            LOGGER.error("读取配置文件[{}]发生错误", fileName);
        }
        return properties;
    }
}
