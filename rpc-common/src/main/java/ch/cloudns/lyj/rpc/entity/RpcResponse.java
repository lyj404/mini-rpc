package ch.cloudns.lyj.rpc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ch.cloudns.lyj.rpc.enums.ResponseCodeEnum;

import java.io.Serializable;

/**
 * @Description 提供者向消费者返回的对象
 * @Date 2024/6/9
 * @Author lyj
 */
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 请求ID
     */
    private String requestId;

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

    /**
     * 成功的响应
     * @param data 响应的数据
     * @return 响应结果集
     * @param <T> 参数类型不确定
     */
    public static <T> RpcResponse<T> success(T data, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCodeEnum codeEnum, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(codeEnum.getCode());
        response.setMessage(codeEnum.getMessage());
        return response;
    }
}
