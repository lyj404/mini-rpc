package sh.cloudns.lyj.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 单例工厂
 * @Date 2024/6/12
 * @Author lyj
 */
public class SingletonFactory {
    private static final Map<Class<?>, Object> objectMap = new HashMap<>();

    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz){
            if (instance == null) {
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                    objectMap.put(clazz, instance);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
