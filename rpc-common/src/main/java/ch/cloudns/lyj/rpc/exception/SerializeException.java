package ch.cloudns.lyj.rpc.exception;

/**
 * @Description 序列化异常
 * @Date 2024/6/10
 * @Author lyj
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}
