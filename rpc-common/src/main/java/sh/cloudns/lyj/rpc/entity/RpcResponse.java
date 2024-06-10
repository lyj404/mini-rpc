package sh.cloudns.lyj.rpc.entity;

import lombok.Data;
import sh.cloudns.lyj.rpc.enums.ResponseCodeEnum;

import java.io.Serializable;

/**
 * @Description 提供者向消费者返回的对象
 * @Date 2024/6/9
 * @Author lyj
 */
@Data
public class RpcResponse<T> implements Serializable {
    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应状态信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public RpcResponse(){}

    /**
     * 成功的响应
     * @param data 响应的数据
     * @return 响应结果集
     * @param <T> 参数类型不确定
     */
    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCodeEnum codeEnum){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(codeEnum.getCode());
        response.setMessage(codeEnum.getMessage());
        return response;
    }
}
