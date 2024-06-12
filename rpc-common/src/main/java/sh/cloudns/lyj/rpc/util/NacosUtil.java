package sh.cloudns.lyj.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @description 管理nacos连接的工具类
 * @Date 2024/6/12
 * @Author lyj
 */
public class NacosUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService NAMING_SERVICE;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress ADDRESS;

    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        NAMING_SERVICE = getNacosNamingService();
    }

    public static NamingService getNacosNamingService(){
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e){
            LOGGER.error("连接nacos时发生错误：", e);
            throw new RpcException(RpcErrorEnum.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException{
        NAMING_SERVICE.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.ADDRESS = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return NAMING_SERVICE.getAllInstances(serviceName);
    }

    public static void clearRegister(){
        if (!serviceNames.isEmpty() && ADDRESS != null){
            String host = ADDRESS.getHostName();
            int port = ADDRESS.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    NAMING_SERVICE.deregisterInstance(serviceName, host, port);
                } catch (NacosException e){
                    LOGGER.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}