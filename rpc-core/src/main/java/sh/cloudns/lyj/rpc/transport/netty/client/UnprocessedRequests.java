package sh.cloudns.lyj.rpc.transport.netty.client;

import sh.cloudns.lyj.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 管理未处理的 RPC 请求响应的类
 * 用于存储和检索与特定请求 ID 相关联的 CompletableFuture，以便在请求处理完成时通知调用者
 * @author: lyj
 * @date: 2024/6/13 17:13
 */
public class UnprocessedRequests {
    /**
     * 用于存储请求 ID 和对应的 CompletableFuture<RpcResponse<?>>
     */
    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse<?>>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    /**
     * 将请求 ID 和对应的 CompletableFuture 添加到 unprocessedResponseFutures 中。
     * @param requestId 请求的 ID。
     * @param future 与请求 ID 相关联的 CompletableFuture，用于异步处理 RPC 响应。
     */
    public void put(String requestId, CompletableFuture<RpcResponse<?>> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    /**
     * 从 unprocessedResponseFutures 中移除指定请求 ID 的条目。
     * @param requestId 要移除的请求的 ID。
     */
    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    /**
     * 完成与给定 RpcResponse 相关联的 CompletableFuture。
     * 当 RPC 响应被处理后，使用响应的请求 ID 从 unprocessedResponseFutures 中检索对应的 future 并完成它。
     * @param rpcResponse 已处理的 RpcResponse 实例。
     */
    public void complete(RpcResponse<?> rpcResponse) {
        // 从 unprocessedResponseFutures 中移除与请求 ID 相关联的 future
        CompletableFuture<RpcResponse<?>> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (future != null) {
            // 如果找到对应的 future，则使用 rpcResponse 完成它
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
