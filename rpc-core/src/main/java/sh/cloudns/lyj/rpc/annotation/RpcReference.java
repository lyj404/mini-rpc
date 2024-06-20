package sh.cloudns.lyj.rpc.annotation;

/**
 * @author: liyj
 * @date: 2024/6/20 19:30
 */
public @interface RpcReference {

    /**
     * 服务组，默认空字符
     * @return 服务组
     */
    String group() default "";
}
