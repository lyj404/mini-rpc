package sh.cloudns.lyj.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 表示服务提供类，用于远程接口的实现类
 * @Author: lyj
 * @Date: 2024/6/14 11:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String name() default "";

    String group() default "";
}
