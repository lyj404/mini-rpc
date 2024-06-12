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
import java.util.List;

/**
 * @description 管理nacos连接的工具类
 * @Date 2024/6/12
 * @Author lyj
 */
public class NacosUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosUtil.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";

    public static NamingService getNacosNamingService(){
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e){
            LOGGER.error("连接nacos时发生错误：", e);
            throw new RpcException(RpcErrorEnum.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress address) throws NacosException{
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
    }

    public static List<Instance> getAllInstance(NamingService namingService, String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }
}