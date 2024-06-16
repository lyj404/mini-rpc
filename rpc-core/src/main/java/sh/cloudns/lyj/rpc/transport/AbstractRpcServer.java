package sh.cloudns.lyj.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.annotation.Service;
import sh.cloudns.lyj.rpc.annotation.ServiceScan;
import sh.cloudns.lyj.rpc.enums.RpcErrorEnum;
import sh.cloudns.lyj.rpc.exception.RpcException;
import sh.cloudns.lyj.rpc.provider.ServiceProvider;
import sh.cloudns.lyj.rpc.registry.ServiceRegistry;
import sh.cloudns.lyj.rpc.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author: lyj
 * @date: 2024/6/14 11:58
 */
public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 扫描服务的方法，用于查找并注册服务
     */
    public void scanServices() {
        // 获取启动类的全名
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            // 检查启动类是否有 @ServiceScan 注解
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺 @ServiceScan少注解");
                throw new RpcException(RpcErrorEnum.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            // 启动类未找到异常处理
            logger.error("启动类未找到");
            throw new RpcException(RpcErrorEnum.UNKNOWN_ERROR);
        }
        // 获取基础包名
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 获取包下所有的类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (var clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                // 检查是否有 @Service 注解
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    // 实例化服务类
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    logger.error("创建 " + clazz + " 时发生错误");
                    continue;
                }
                // 根据服务名进行服务注册
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (var oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    /**
     * 实现 RpcServer 接口的 publishService 方法，用于发布服务
     * @param service 服务实例
     * @param serviceName 服务的类
     * @param <T> 泛型
     */
    @Override
    public <T> void publishService(T service, String serviceName) {
        // 将服务和其名称添加到服务提供者中
        serviceProvider.addServiceProvider(service, serviceName);
        // 在服务注册中心注册服务
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
