package sh.cloudns.lyj.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 单例工厂
 * @Date 2024/6/12
 * @Author lyj
 */
public class SingletonFactory {
    private static final Map<Class<?>, Object> objectMap = new HashMap<>(16);

    private SingletonFactory(){}

    /**
     * 用于获取 Class<T> 类型的单例实例
     * @param clazz 类型
     * @return 单例实例
     * @param <T> 泛型
     */
    public static <T> T getInstance(Class<T> clazz) {
        // 从 objectMap 中获取 clazz 类型的实例
        Object instance;
        // 对 clazz 使用 synchronized 同步锁，确保线程安全
        synchronized (clazz){
            instance = objectMap.get(clazz);
            // 如果 instance 为 null，说明尚未创建实例
            if (instance == null) {
                try {
                    // 通过反射调用 clazz 的无参构造函数来创建实例
                    instance = clazz.getDeclaredConstructor().newInstance();
                    // 将新创建的实例存储到 objectMap 中
                    objectMap.put(clazz, instance);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        // 将 Object 类型的实例向下转型为 T 类型，并返回
        return clazz.cast(instance);
    }
}
