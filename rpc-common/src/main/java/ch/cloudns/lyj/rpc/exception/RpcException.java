package ch.cloudns.lyj.rpc.exception;

import ch.cloudns.lyj.rpc.enums.RpcErrorEnum;

/**
 * @Description RPC调用异常
 * @Date 2024/6/9
 * @Author lyj
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorEnum errorEnum, String detail){
        super(errorEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause){
        super(message, cause);
    }

    public RpcException(RpcErrorEnum errorEnum){
        super(errorEnum.getMessage());
    }
}
